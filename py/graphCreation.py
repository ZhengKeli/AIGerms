import tensorflow as tf

# actor network
feel = tf.placeholder(tf.float32, name="feel")  # [-1,7]

input_actor1 = feel  # [-1,7]
weights_actor1 = tf.Variable(tf.random_uniform([7, 30], -1.0, 1.0))
bias_actor1 = tf.Variable(tf.random_uniform([30], -1.0, 1.0))
output_actor1 = tf.nn.tanh(tf.matmul(input_actor1, weights_actor1) + bias_actor1)  # [-1,30]

input_actor2 = output_actor1  # [-1,30]
weights_actor2 = tf.Variable(tf.random_uniform([30, 2], -1.0, 1.0))  # [-1,2]
bias_actor2 = tf.Variable(tf.random_uniform([2], -1.0, 1.0))
output_actor2 = tf.nn.tanh(tf.matmul(input_actor2, weights_actor2) + bias_actor2)  # [-1,2]

act = tf.multiply(output_actor2, 1.0, "act")  # [-1,2]

# critic network
input_critic1 = tf.concat([feel, act], 1)  # [-1,9]
weights_critic1 = tf.Variable(tf.random_uniform([9, 50], -1.0, 1.0))
bias_critic1 = tf.Variable(tf.random_uniform([50], -1.0, 1.0))
output_critic1 = tf.nn.tanh(tf.matmul(input_critic1, weights_critic1) + bias_critic1)  # [-1,50]

input_critic2 = output_critic1
weights_critic2 = tf.Variable(tf.random_uniform([50, 50], -1.0, 1.0))
bias_critic2 = tf.Variable(tf.random_uniform([50], -1.0, 1.0))
output_critic2 = tf.nn.tanh(tf.matmul(input_critic2, weights_critic2) + bias_critic2)  # [-1,50]

input_critic3 = output_critic2
weights_critic3 = tf.Variable(tf.random_uniform([50, 1], -1.0, 1.0))
bias_critic3 = tf.Variable(tf.random_uniform([1], -1.0, 1.0))
output_critic3 = tf.nn.tanh(tf.matmul(input_critic3, weights_critic3) + bias_critic3)  # [-1,1]

ass_loss = tf.multiply(tf.reduce_sum(output_critic3, -1), 1.2, name="ass_loss")  # [-1]


# buffered critic network
input_buffered_critic1 = tf.concat([feel, act], 1)  # [-1,9]
weights_buffered_critic1 = tf.Variable(tf.random_uniform([9, 50], -1.0, 1.0))
bias_buffered_critic1 = tf.Variable(tf.random_uniform([50], -1.0, 1.0))
output_buffered_critic1 = tf.nn.tanh(tf.matmul(input_critic1, weights_critic1) + bias_critic1)  # [-1,50]

input_buffered_critic2 = output_critic1
weights_buffered_critic2 = tf.Variable(tf.random_uniform([50, 50], -1.0, 1.0))
bias_buffered_critic2 = tf.Variable(tf.random_uniform([50], -1.0, 1.0))
output_buffered_critic2 = tf.nn.tanh(tf.matmul(input_critic2, weights_critic2) + bias_critic2)  # [-1,50]

input_buffered_critic3 = output_critic2
weights_buffered_critic3 = tf.Variable(tf.random_uniform([50, 1], -1.0, 1.0))
bias_buffered_critic3 = tf.Variable(tf.random_uniform([1], -1.0, 1.0))
output_buffered_critic3 = tf.nn.tanh(tf.matmul(input_critic3, weights_critic3) + bias_critic3)  # [-1,1]

buffered_ass_loss = tf.multiply(tf.reduce_sum(output_buffered_critic3, -1), 1.2)  # [-1]


# train
real_loss = tf.placeholder(tf.float32, name="real_loss")  # [-1]
loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [-1]

vars_actor = (weights_actor1, bias_actor1, weights_actor2, bias_actor2)
train_actor = tf.train.AdamOptimizer(0.01) \
    .minimize(tf.reduce_sum(buffered_ass_loss), var_list=vars_actor, name="train_actor")

vars_critic = (weights_critic1, bias_critic1, weights_critic2, bias_critic2, weights_critic3, bias_critic3)
train_critic = tf.train.AdamOptimizer(0.01) \
    .minimize(tf.reduce_sum(loss_loss), var_list=vars_critic, name="train_critic")

assign_rate = tf.Variable(0.05, dtype=tf.float32, name="rate_assign_critic")
assign_critic = tf.group(
    tf.assign_add(weights_buffered_critic1, (weights_critic1 - weights_buffered_critic1) / assign_rate),
    tf.assign_add(weights_buffered_critic2, (weights_critic2 - weights_buffered_critic2) / assign_rate),
    tf.assign_add(weights_buffered_critic3, (weights_critic3 - weights_buffered_critic3) / assign_rate),
    tf.assign_add(bias_buffered_critic1, (bias_critic1 - bias_buffered_critic1) / assign_rate),
    tf.assign_add(bias_buffered_critic2, (bias_critic2 - bias_buffered_critic2) / assign_rate),
    tf.assign_add(bias_buffered_critic3, (bias_critic3 - bias_buffered_critic3) / assign_rate),
    name="assign_critic")

# session
sess = tf.Session()
sess.run(tf.global_variables_initializer())
saver = tf.train.Saver()
saver.save(sess, "./graph/aiGerms")
print("graph saved.")
