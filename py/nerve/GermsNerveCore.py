import tensorflow as tf

from nerve.FullConnectedNetwork import FullConnectedNetwork
from nerve.NerveCore import NerveCore


class GermsNerveCore(NerveCore):
    def __init__(self, path, name):
        super().__init__(path, name)
        with self.graph.as_default() as graph:
            self.feel = graph.get_tensor_by_name("feel:0")
            self.act = graph.get_tensor_by_name("act:0")
            self.actor_keep = graph.get_tensor_by_name("actor_keep:0")
            self.critic_keep = graph.get_tensor_by_name("critic_keep:0")

            self.ass_loss = graph.get_tensor_by_name("ass_loss:0")
            self.real_loss = graph.get_tensor_by_name("real_loss:0")
            self.loss_loss = graph.get_tensor_by_name("loss_loss:0")

            self.train_actor = graph.get_operation_by_name("train_actor")
            self.train_critic = graph.get_operation_by_name("train_critic")

    def create_graph(self):
        with self.graph.as_default():
            # actor network
            feel = tf.placeholder(tf.float32, name="feel")  # [-1,6]
            actor_keep = tf.Variable(1.0, name="actor_keep")
            feel_network = FullConnectedNetwork([6, 64, 64, 64, 16], tf.nn.tanh, actor_keep)
            feel_result = feel_network.apply(feel)
            act_network = FullConnectedNetwork([16, 16, 2], tf.nn.tanh, actor_keep)
            act_result = act_network.apply(feel_result)
            act = tf.multiply(act_result, 1.0, "act")  # [-1,2]

            # critic network
            critic_keep = tf.Variable(1.0, name="critic_keep")
            critic_in = tf.concat([feel_result, act], 1)  # [-1,18]
            critic_network = FullConnectedNetwork([18, 64, 64, 64, 1], tf.nn.tanh, critic_keep)
            critic_result = critic_network.apply(critic_in)
            ass_loss = tf.multiply(tf.reduce_sum(critic_result, 1), 1.0, "ass_loss")  # [-1]

            # train
            real_loss = tf.placeholder(tf.float32, name="real_loss")  # [-1]
            loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [-1]

            vars_actor = [feel_network.weights,feel_network.bias,act_network.weights,act_network.bias]
            train_actor = tf.train.AdamOptimizer(1e-2) \
                .minimize(tf.reduce_sum(ass_loss), var_list=vars_actor, name="train_actor")

            vars_critic = [critic_network.weights, critic_network.bias]
            train_critic = tf.train.AdamOptimizer(1e-2) \
                .minimize(tf.reduce_sum(loss_loss), var_list=vars_critic, name="train_critic")

            self.sess.run(tf.global_variables_initializer())

    def run_actor(self, val_feel):
        return self.sess.run(
            fetches=self.act,
            feed_dict={
                self.feel: val_feel,
                self.actor_keep: 1.0
            }
        )

    def run_train_critic(self, val_feel, val_act, val_real_loss):
        return self.sess.run(
            fetches=[
                self.train_critic,
                self.ass_loss,
                self.loss_loss
            ],
            feed_dict={
                self.feel: val_feel,
                self.act: val_act,
                self.real_loss: val_real_loss,
                self.critic_keep: 1.0,
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
                self.actor_keep: 1.0
            }
        )
