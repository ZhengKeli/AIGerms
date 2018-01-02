import tensorflow as tf

from nerve.FullConnectedNetwork import FullConnectedNetwork
from nerve.NerveCore import NerveCore


class GermsNerveCore(NerveCore):
    def __init__(self, path, name):
        super().__init__(path, name)
        with self.graph.as_default() as graph:
            self.feel = graph.get_tensor_by_name("feel:0")
            self.act = graph.get_tensor_by_name("act:0")

            self.ass_loss = graph.get_tensor_by_name("ass_loss:0")
            self.real_loss = graph.get_tensor_by_name("real_loss:0")
            self.loss_loss = graph.get_tensor_by_name("loss_loss:0")

            self.learning_rate_actor = graph.get_tensor_by_name("learning_rate_actor:0")
            self.learning_rate_critic = graph.get_tensor_by_name("learning_rate_critic:0")
            self.train_actor = graph.get_operation_by_name("train_actor")
            self.train_critic = graph.get_operation_by_name("train_critic")

    def create_graph(self):
        with self.graph.as_default():

            feel = tf.placeholder(tf.float32, [None, 6], name="feel")  # [-1,6]

            # actor network
            feel_network = FullConnectedNetwork([3, 16, 15], tf.nn.tanh)
            act_network = FullConnectedNetwork([15, 4], tf.nn.tanh)

            feel_x = tf.stack([feel[:, 0], feel[:, 2], feel[:, 4]], -1)  # [-1,3]
            feel_y = tf.stack([feel[:, 1], feel[:, 3], feel[:, 5]], -1)  # [-1,3]

            perception_x = feel_network.apply(feel_x)  # [-1,15]
            perception_y = feel_network.apply(feel_y)  # [-1,15]

            act_x = act_network.apply(perception_x)  # [-1,4]
            act_x = tf.reduce_mean(act_x, -1, keep_dims=True)  # [-1,1]
            act_y = act_network.apply(perception_y)  # [-1,4]
            act_y = tf.reduce_mean(act_y, -1, keep_dims=True)  # [-1,1]

            act = tf.concat([act_x, act_y], -1, "act")  # [-1,2]

            # critic network
            critic_network = FullConnectedNetwork([16, 8], tf.nn.tanh)

            situation_x = tf.concat([perception_x, act[:, 0:1]], 1)  # [-1,16]
            situation_y = tf.concat([perception_y, act[:, 1:2]], 1)  # [-1,16]

            critic_x = critic_network.apply(situation_x)  # [-1,8]
            critic_y = critic_network.apply(situation_y)  # [-1,8]

            ass_loss = tf.concat([critic_x, critic_y], -1)  # [-1,16]
            ass_loss = tf.reduce_mean(ass_loss, -1, name="ass_loss")  # [-1]
            ave_ass_loss = tf.reduce_mean(ass_loss)

            # train
            real_loss = tf.placeholder(tf.float32, name="real_loss")  # [-1]
            loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [-1]
            ave_loss_loss = tf.reduce_mean(loss_loss)

            learning_rate_actor = tf.Variable(5e-3, name="learning_rate_actor")
            tf.train.AdamOptimizer(learning_rate_actor).minimize(
                name="train_actor",
                loss=ave_ass_loss,
                var_list=[
                    feel_network.weights, feel_network.bias,
                    act_network.weights, act_network.bias
                ],
            )

            learning_rate_critic = tf.Variable(5e-3, name="learning_rate_critic")
            tf.train.AdamOptimizer(learning_rate_critic).minimize(
                name="train_critic",
                loss=ave_loss_loss,
                var_list=[
                    feel_network.weights, feel_network.bias,
                    critic_network.weights, critic_network.bias
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

    def run_critic(self, val_feel, val_act):
        return self.sess.run(
            fetches=self.ass_loss,
            feed_dict={
                self.feel: val_feel,
                self.act: val_act
            }
        )

    def run_train_critic(self, val_feel, val_act, val_real_loss):
        return self.sess.run(
            fetches=[
                self.train_critic,
                self.ass_loss,
                self.loss_loss,
            ],
            feed_dict={
                self.feel: val_feel,
                self.act: val_act,
                self.real_loss: val_real_loss,
            }
        )

    def run_train_actor(self, val_feel):
        return self.sess.run(
            fetches=[
                self.train_actor,
                self.ass_loss],
            feed_dict={
                self.feel: val_feel,
                self.real_loss: None,
            }
        )
