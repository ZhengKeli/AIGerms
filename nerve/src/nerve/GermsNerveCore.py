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

            self.man_act = graph.get_tensor_by_name("man_act:0")
            self.man_loss = graph.get_tensor_by_name("man_loss:0")
            self.ave_man_loss = graph.get_tensor_by_name("ave_man_loss:0")
            self.learning_rate_actor = graph.get_tensor_by_name("learning_rate_actor:0")
            self.man_train_actor = graph.get_operation_by_name("man_train_actor")

            self.log_feels = graph.get_tensor_by_name("log_feels:0")
            self.log_acts = graph.get_tensor_by_name("log_acts:0")

            self.memory_base = graph.get_tensor_by_name("memory_base:0")

            self.ass_loss = graph.get_tensor_by_name("ass_loss:0")
            self.ave_ass_loss = graph.get_tensor_by_name("ave_ass_loss:0")

            self.loss_loss = graph.get_tensor_by_name("loss_loss:0")
            self.ave_loss_loss = graph.get_tensor_by_name("ave_loss_loss:0")
            self.real_loss = graph.get_tensor_by_name("real_loss:0")
            self.ave_real_loss = graph.get_tensor_by_name("ave_real_loss:0")
            self.learning_rate_actor = graph.get_tensor_by_name("learning_rate_actor:0")
            self.critic_train_actor = graph.get_operation_by_name("critic_train_actor")
            self.learning_rate_critic = graph.get_tensor_by_name("learning_rate_critic:0")
            self.critic_train_ass = graph.get_operation_by_name("critic_train_ass")

            self.summary_ass_loss = tf.summary.scalar("summary_ass_loss", self.ave_ass_loss)
            self.summary_loss_loss = tf.summary.scalar("summary_loss_loss", self.ave_loss_loss)
            self.summary_real_loss = tf.summary.scalar("summary_real_loss", self.ave_real_loss)
            self.summary_all = tf.summary.merge_all()
        self.summary_file_writer = tf.summary.FileWriter("../logs", graph=self.graph)
        self.critic_train_step = 0
        self.actor_train_step = 0

    def create_graph(self):
        with self.graph.as_default():
            feel_network = MultiDenseNetwork([3, 16, 3], tf.nn.tanh)
            feel = tf.placeholder(tf.float32, [None, 6], name="feel")  # [bs,6]
            with tf.name_scope("feel_splitting"):
                feel = [tf.gather(feel, [0, 2, 4], axis=-1), tf.gather(feel, [1, 3, 5], axis=-1)]  # 2*[bs,3]
                feel = tf.stack(feel, -2)  # [bs,2,3]
            perception = feel_network.apply(feel, name_scope="feel_MultiDense")  # [bs,2,3]

            actor_network = MultiDenseNetwork([3, 16, 4], tf.nn.tanh)
            act = actor_network.apply(perception, name_scope="actor_MultiDense")  # [bs,2,4]
            act = tf.reduce_mean(act, -1, False, name="act")  # [bs,2]

            man_act = tf.placeholder(tf.float32, [None, 2], name="man_act")  # [bs,2]
            man_loss = tf.reduce_sum(tf.square(act - man_act), -1, False, name="man_loss")  # [bs]
            ave_man_loss = tf.reduce_sum(man_loss, name="ave_man_loss")  # 0

            learning_rate_man = tf.Variable(2e-3, name="learning_rate_actor")
            tf.train.AdamOptimizer(learning_rate_man).minimize(
                name="man_train_actor",
                loss=ave_man_loss,
                var_list=[
                    feel_network.weights, feel_network.bias,
                    actor_network.weights, actor_network.bias
                ],
            )

            # with tf.name_scope("log"):
            rc = 4
            log_feels = tf.placeholder(tf.float32, [None, rc, 6], name="log_feels")  # [bs,rc,6]
            with tf.name_scope("log_feels_splitting"):
                log_feels = [tf.gather(log_feels, [0, 2, 4], axis=-1), tf.gather(log_feels, [1, 3, 5], axis=-1)]  # 2*[bs,rc,3]
                log_feels = tf.stack(log_feels, -2)  # [bs,rc,2,3]
            log_perceptions = feel_network.apply(log_feels, name_scope="log_feels_MultiDense")  # [bs,rc,2,3]

            log_acts = actor_network.apply(log_perceptions, name_scope="log_acts_MultiDense")  # [bs,rc,2,4]
            log_acts = tf.reduce_mean(log_acts, -1, False, name="log_acts")  # [bs,rc,2]

            with tf.name_scope("log_merging_situations"):
                log_situations = tf.concat([log_perceptions, tf.expand_dims(log_acts, -1)], -1)  # [bs,rc,2,4]
                log_situation_list = tf.unstack(log_situations, rc, -3)  # rc*[bs,2,4]

            memory_base = tf.placeholder(tf.float32, [None, 4], name="memory_base")  # [bs,4]
            memory_base = tf.stack([memory_base, memory_base], -2)  # [bs,2,4]
            with tf.name_scope("rnn"):
                cell_network = MultiDenseNetwork([8, 4], tf.nn.tanh)
                cycle_output = memory_base  # [bs,2,4]
                for i in range(rc):
                    cycle_input = tf.concat([log_situation_list[i], cycle_output], -1)  # [bs,2,8]
                    cycle_output = cell_network.apply(cycle_input, name_scope="cell_MultiDense")  # [bs,2,4]

            ass_network = MultiDenseNetwork([4, 4], tf.nn.tanh)
            ass_loss = ass_network.apply(cycle_output, name_scope="ass_MultiDense")  # [bs,2,4]
            ass_loss = tf.reduce_mean(ass_loss, -1)  # [bs,2]
            ass_loss = tf.reduce_mean(ass_loss, -1, name="ass_loss")  # [bs]
            ave_ass_loss = tf.reduce_mean(ass_loss, name="ave_ass_loss")  # 0

            real_loss = tf.placeholder(tf.float32, name="real_loss")  # [bs]
            ave_real_loss = tf.reduce_mean(real_loss, name="ave_real_loss")  # 0
            loss_loss = tf.divide(tf.abs(ass_loss - real_loss), tf.abs(real_loss), name="loss_loss")  # [bs]
            ave_loss_loss = tf.reduce_mean(loss_loss, name="ave_loss_loss")  # 0

            learning_rate_actor = tf.Variable(2e-3, name="learning_rate_actor")
            tf.train.AdamOptimizer(learning_rate_actor).minimize(
                name="critic_train_actor",
                loss=ave_ass_loss,
                var_list=[
                    actor_network.weights, actor_network.bias
                ],
            )

            learning_rate_critic = tf.Variable(2e-3, name="learning_rate_critic")
            tf.train.AdamOptimizer(learning_rate_critic).minimize(
                name="critic_train_ass",
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
        _, val_summary_loss_loss, val_summary_real_loss = self.sess.run(
            fetches=[
                self.critic_train_ass,
                self.summary_loss_loss,
                self.summary_real_loss
            ],
            feed_dict={
                self.log_feels: val_log_feels,
                self.log_acts: val_log_acts,
                self.real_loss: val_real_loss,
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 4], np.float32)
            }
        )
        self.summary_file_writer.add_summary(val_summary_loss_loss, self.critic_train_step)
        self.summary_file_writer.add_summary(val_summary_real_loss, self.critic_train_step)
        self.summary_file_writer.flush()
        self.critic_train_step += 1

    def run_train_actor(self, val_log_feels):
        _, val_summary_ass_loss = self.sess.run(
            fetches=[
                self.critic_train_actor,
                self.summary_ass_loss
            ],
            feed_dict={
                self.log_feels: val_log_feels,
                self.real_loss: None,
                self.memory_base: np.zeros([np.shape(val_log_feels)[0], 4], np.float32)
            }
        )
        self.summary_file_writer.add_summary(val_summary_ass_loss, self.actor_train_step)
        self.summary_file_writer.flush()
        self.actor_train_step += 1
