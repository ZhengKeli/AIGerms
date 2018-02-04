import numpy as np
import tensorflow as tf

from TFUtils.core.NerveCore import NerveCore
from TFUtils.network.MultiDenseNetwork import MultiDenseNetwork


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
            feel_network = MultiDenseNetwork([3, 16, 3], tf.nn.tanh)
            actor_network = MultiDenseNetwork([3, 16, 4], tf.nn.tanh)
            cell_network = MultiDenseNetwork([8, 4], tf.nn.tanh)
            ass_network = MultiDenseNetwork([4, 4], tf.nn.tanh)

            # actor line
            feel = tf.placeholder(tf.float32, [None, 6], name="feel")  # [bs,6]
            feel = [tf.gather(feel, [0, 2, 4], axis=-1), tf.gather(feel, [1, 3, 5], axis=-1)]  # 2*[bs,3]
            feel = tf.stack(feel, -2)  # [bs,2,3]
            perception = feel_network.apply(feel)  # [bs,2,3]

            act = actor_network.apply(perception)  # [bs,2,4]
            act = tf.reduce_mean(act, -1, False, name="act")  # [bs,2]

            # critic line
            log_feels = tf.placeholder(tf.float32, [None, 8, 6], name="log_feels")  # [bs,8,6]
            log_feels = [tf.gather(log_feels, [0, 2, 4], axis=-1), tf.gather(log_feels, [1, 3, 5], axis=-1)]  # 2*[bs,8,3]
            log_feels = tf.stack(log_feels, -2)  # [bs,8,2,3]
            log_perceptions = feel_network.apply(log_feels)  # [bs,8,2,3]

            log_acts = actor_network.apply(log_perceptions)  # [bs,8,2,4]
            log_acts = tf.reduce_mean(log_acts, -1, False, name="log_acts")  # [bs,8,2]
            log_acts = tf.stack([log_acts], -1)  # [bs,8,2,1]

            log_situations = tf.concat([log_perceptions, log_acts], -1)  # [bs,8,2,4]
            log_situation_list = tf.unstack(log_situations, 8, -3)  # 8*[bs,2,4]

            memory_base = tf.placeholder(tf.float32, [None, 4], name="memory_base")  # [bs,4]
            memory_base = tf.stack([memory_base, memory_base], -2)  # [bs,2,4]
            cycle_output = memory_base  # [bs,2,4]
            for i in range(8):
                cycle_input = tf.concat([log_situation_list[i], cycle_output], -1)  # [bs,2,8]
                cycle_output = cell_network.apply(cycle_input)  # [bs,2,4]

            ass_loss = ass_network.apply(cycle_output)  # [bs,2,4]
            ass_loss = tf.reduce_mean(ass_loss, -1)  # [bs,2]
            ass_loss = tf.reduce_mean(ass_loss, -1, name="ass_loss")  # [bs]
            ave_ass_loss = tf.reduce_mean(ass_loss, name="ave_ass_loss")  # 0

            # train network
            real_loss = tf.placeholder(tf.float32, name="real_loss")  # [bs]
            loss_loss = tf.abs(ass_loss - real_loss, name="loss_loss")  # [bs]
            ave_loss_loss = tf.reduce_mean(loss_loss, name="ave_loss_loss")  # 0

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
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 4], np.float32)
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
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 4], np.float32)
            }
        )
