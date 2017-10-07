import tensorflow as tf


class TFNerveCore:
    def __init__(self, path, name):
        self.sess = tf.Session()
        self.path = path

        try:
            self.load_graph(path,name)
        except OSError as e:
            print(e)
            self.create_graph()
            self.save_graph(path,name)

        graph = self.sess.graph

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
        # actor network
        feel = tf.placeholder(tf.float32, name="feel")  # [-1,7]
        actor_keep = tf.Variable(1.0,name="actor_keep")
        actor_network = FullConnectedNetwork(feel, [7, 20, 20, 2], actor_keep)
        act = tf.multiply(actor_network.outputs, 1.0, "act")  # [-1,2]

        # critic network
        log = tf.concat([feel, act], 1)  # [-1,9]
        critic_keep = tf.Variable(1.0,name="critic_keep")
        critic_network = FullConnectedNetwork(log, [9, 30, 30, 30, 1], critic_keep)
        ass_loss = tf.multiply(tf.reduce_sum(critic_network.outputs, -1), 1.0, "ass_loss")  # [-1]

        # train
        real_loss = tf.placeholder(tf.float32, name="real_loss")  # [-1]
        loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [-1]

        vars_actor = [actor_network.weights, actor_network.bias]
        train_actor = tf.train.AdamOptimizer(0.01) \
            .minimize(tf.reduce_sum(ass_loss), var_list=vars_actor, name="train_actor")

        vars_critic = [critic_network.weights, critic_network.bias]
        train_critic = tf.train.AdamOptimizer(0.01) \
            .minimize(tf.reduce_sum(loss_loss), var_list=vars_critic, name="train_critic")

        self.sess.run(tf.global_variables_initializer())

    def load_graph(self, path, name):
        tf.train.import_meta_graph(path + "/" + name + ".meta") \
            .restore(self.sess, tf.train.latest_checkpoint(path))

    def save_graph(self, path, name):
        saver = tf.train.Saver()
        saver.save(self.sess, path + "/" + name)

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
                self.critic_keep: 0.8
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
                self.actor_keep: 0.9
            }
        )


class FullConnectedNetwork:
    def __init__(self, inputs, shape, keep=None):
        self.inputs = inputs
        self.weights = []
        self.bias = []

        last_output = self.inputs
        for i in range(len(shape) - 1):
            layer_input_size = shape[i]
            layer_output_size = shape[i + 1]

            layer_weights = tf.Variable(tf.random_uniform(
                shape=[layer_input_size, layer_output_size],
                minval=-1.0, maxval=1.0
            ))
            layer_bias = tf.Variable(tf.random_uniform(
                shape=[layer_output_size],
                minval=-1.0, maxval=1.0
            ))
            self.weights.append(layer_weights)
            self.bias.append(layer_bias)

            layer_input = last_output
            layer_raw_output = tf.matmul(layer_input, layer_weights) + layer_bias

            if keep is None:
                layer_output = tf.tanh(layer_raw_output)
            else:
                layer_output = tf.nn.dropout(tf.tanh(layer_raw_output), keep)

            last_output = layer_output

        self.outputs = last_output
