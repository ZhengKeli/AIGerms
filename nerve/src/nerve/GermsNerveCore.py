import tensorflow as tf

from nerve.FullConnectedNetwork import FullConnectedNetwork
from nerve.NerveCore import NerveCore


class GermsNerveCore(NerveCore):
    def __init__(self, path, name):
        super().__init__(path, name)
        with self.graph.as_default() as graph:
            self.feel = graph.get_tensor_by_name("feel:0")
            self.act = graph.get_tensor_by_name("act:0")
            self.logs = graph.get_tensor_by_name("logs:0")
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
            class ActorNetwork(FullConnectedNetwork):
                def __init__(self, shape, activation):
                    super().__init__(shape, activation)

                def apply(self, inputs, name=None):
                    act_x, act_y = tf.split(super().apply(inputs, None), 2, -1)
                    act_x = tf.reduce_mean(act_x, -1, keep_dims=True)
                    act_y = tf.reduce_mean(act_y, -1, keep_dims=True)
                    return tf.concat([act_x, act_y], -1, name)  # [..,2]

            with tf.name_scope("feel"):
                feel_network = FullConnectedNetwork([6, 16, 14], tf.nn.tanh)

                feel = tf.placeholder(tf.float32, [None, 6], name="feel")  # [bs,6]
                perception = feel_network.apply(feel)  # [bs,14]

                log_feels = tf.placeholder(tf.float32, [None, 8, 6], name="log_feels")  # [bs,8,6]
                log_perceptions = feel_network.apply(log_feels)  # [bs,8,14]

            with tf.name_scope("actor"):
                actor_network = ActorNetwork([14, 16, 8], tf.nn.tanh)

                act = actor_network.apply(perception, "act")  # [bs,2]
                log_acts = actor_network.apply(log_perceptions, name="log_acts")  # [bs,8,2]

            with tf.name_scope("critic"):
                cell_network = FullConnectedNetwork([32, 16, 16], tf.nn.tanh)
                ass_network = FullConnectedNetwork([16, 8], tf.nn.tanh)

                log_situations = tf.concat([log_perceptions, log_acts], -1)  # [bs,8,16]
                log_situation_list = tf.unstack(log_situations, 8, -2)  # 8*[bs,16]

                memory_base = tf.placeholder(tf.float32, [None, 16], name="memory_base")  # [bs,16]
                cycle_output = memory_base  # [bs,16]
                for i in range(8):
                    cycle_input = tf.concat([log_situation_list[i], cycle_output], -1)  # [bs,32]
                    cycle_output = cell_network.apply(cycle_input)  # [bs,16]

                ass_loss = ass_network.apply(cycle_output)  # [bs,8]
                ass_loss = tf.reduce_mean(ass_loss, -1, name="ass_loss")  # [bs]
                ave_ass_loss = tf.reduce_mean(ass_loss, name="ave_ass_loss")

            with tf.name_scope("train"):
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
            }
        )
