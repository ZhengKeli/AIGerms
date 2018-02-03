import numpy as np
import tensorflow as tf

from TFUtils.core.NerveCore import NerveCore
from TFUtils.network.FullyConnectedNetwork import FullyConnectedNetwork


class GermsNerveCore(NerveCore):
    def __init__(self, path, name):
        super().__init__(path, name)
        with self.graph.as_default() as graph:
            self.feel = graph.get_tensor_by_name("feel:0")
            self.act = graph.get_tensor_by_name("act:0")
            self.memory_base = graph.get_tensor_by_name("memory_base:0")

            self.log_feels = graph.get_tensor_by_name("log_feels:0")
            self.log_acts = graph.get_tensor_by_name("log_acts:0")

            self.ass_loss = graph.get_tensor_by_name("ass_loss:0")
            self.real_loss = graph.get_tensor_by_name("real_loss:0")
            self.loss_loss = graph.get_tensor_by_name("loss_loss:0")

            self.learning_rate_actor = graph.get_tensor_by_name("learning_rate_actor:0")
            self.learning_rate_critic = graph.get_tensor_by_name("learning_rate_critic:0")
            self.train_actor = graph.get_operation_by_name("train_actor")
            self.train_critic = graph.get_operation_by_name("train_critic")

    def create_graph(self):
        with self.graph.as_default():
            # feel network
            feel_network = FullyConnectedNetwork([6, 16, 6], tf.nn.tanh)

            feel = tf.placeholder(tf.float32, [None, 6], name="feel")  # [bs,6]
            perception = feel_network.apply(feel)  # [bs,14]

            log_feels = tf.placeholder(tf.float32, [None, 8, 6], name="log_feels")  # [bs,8,6]
            log_perceptions = feel_network.apply(log_feels)  # [bs,8,6]

            # actor network
            class ActorNetwork(FullyConnectedNetwork):
                def __init__(self, shape, activation):
                    super().__init__(shape, activation)

                def apply(self, inputs, name=None):
                    act_x, act_y = tf.split(super().apply(inputs, None), 2, -1)
                    act_x = tf.reduce_mean(act_x, -1, keepdims=True)
                    act_y = tf.reduce_mean(act_y, -1, keepdims=True)
                    return tf.concat([act_x, act_y], -1, name)  # [..,2]

            actor_network = ActorNetwork([6, 16, 8], tf.nn.tanh)

            act = actor_network.apply(perception, "act")  # [bs,2]
            log_acts = actor_network.apply(log_perceptions, name="log_acts")  # [bs,8,2]

            # critic network
            cell_network = FullyConnectedNetwork([16, 8], tf.nn.tanh)
            ass_network = FullyConnectedNetwork([8, 8], tf.nn.tanh)

            log_situations = tf.concat([log_perceptions, log_acts], -1)  # [bs,8,8]
            log_situation_list = tf.unstack(log_situations, 8, -2)  # 8*[bs,8]

            memory_base = tf.placeholder(tf.float32, [None, 8], name="memory_base")  # [bs,8]
            cycle_output = memory_base  # [bs,8]
            for i in range(8):
                cycle_input = tf.concat([log_situation_list[i], cycle_output], -1)  # [bs,16]
                cycle_output = cell_network.apply(cycle_input)  # [bs,8]

            ass_loss = ass_network.apply(cycle_output)  # [bs,8]
            ass_loss = tf.reduce_mean(ass_loss, -1, name="ass_loss")  # [bs]
            ave_ass_loss = tf.reduce_mean(ass_loss, name="ave_ass_loss")

            # train network
            real_loss = tf.placeholder(tf.float32, name="real_loss")  # [bs]
            loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [bs]
            ave_loss_loss = tf.reduce_mean(loss_loss, name="ave_loss_loss")

            learning_rate_actor = tf.Variable(5e-3, name="learning_rate_actor")
            tf.train.AdamOptimizer(learning_rate_actor).minimize(
                name="train_actor",
                loss=ave_ass_loss,
                var_list=[
                    feel_network.weights, feel_network.bias,
                    actor_network.weights, actor_network.bias
                ],
            )

            learning_rate_critic = tf.Variable(5e-3, name="learning_rate_critic")
            tf.train.AdamOptimizer(learning_rate_critic).minimize(
                name="train_critic",
                loss=ave_loss_loss,
                var_list=[
                    feel_network.weights, feel_network.bias,
                    cell_network.weights, cell_network.bias,
                    ass_network.weights, ass_network.bias
                ],
            )

            self.sess.run(tf.global_variables_initializer())

    def run_actor(self, val_feel):
        return self.sess.run(
            fetches=self.act,
            feed_dict={
                self.feel: val_feel
            }
        )

    def run_critic(self, val_log_feels, val_log_acts):
        return self.sess.run(
            fetches=self.ass_loss,
            feed_dict={
                self.log_feels: val_log_feels,
                self.log_acts: val_log_acts
            }
        )

    def run_train_critic(self, val_log_feels, val_log_acts, val_real_loss):
        return self.sess.run(
            fetches=[
                self.train_critic,
                self.ass_loss,
                self.loss_loss,
            ],
            feed_dict={
                self.log_feels: val_log_feels,
                self.log_acts: val_log_acts,
                self.real_loss: val_real_loss,
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 8], np.float32)
            }
        )

    def run_train_actor(self, val_log_feels):
        return self.sess.run(
            fetches=[
                self.train_actor,
                self.ass_loss
            ],
            feed_dict={
                self.log_feels: val_log_feels,
                self.real_loss: None,
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 8], np.float32)
            }
        )
