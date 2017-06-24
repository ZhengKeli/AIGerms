import tensorflow as tf

# feeling network
feel_nutrient = tf.placeholder(tf.float32, name="feel_nutrient")  # [-1,2]
feel_germ = tf.placeholder(tf.float32, name="feel_germ")  # [-1,2]

# actor network
input_actor1 = tf.reshape([feel_nutrient, feel_germ], [-1, 4], "input_actor1")
weights_actor1 = tf.Variable(tf.random_uniform([4, 10]), name="weights_actor1")
bias_actor1 = tf.Variable(tf.random_uniform([10]), name="bias_actor1")
output_actor1 = tf.nn.tanh(tf.matmul(input_actor1, weights_actor1) + bias_actor1, "output_actor1")

input_actor2 = output_actor1
weights_actor2 = tf.Variable(tf.random_uniform([10, 2]), name="weights_actor2")
bias_actor2 = tf.Variable(tf.random_uniform([2]), name="bias_actor2")
output_actor2 = tf.nn.tanh(tf.matmul(input_actor2, weights_actor2) + bias_actor2, "output_actor2")

act_velocity = tf.reshape(output_actor2 * 2.0, [2], "act_velocity")

# critic network
input_critic1 = tf.reshape([feel_nutrient, feel_germ, act_velocity], [-1, 6], "input_critic1")
weights_critic1 = tf.Variable(tf.random_uniform([6, 20]), name="weights_critic1")
bias_critic1 = tf.Variable(tf.random_uniform([20]), name="bias_critic1")
output_critic1 = tf.nn.tanh(tf.matmul(input_critic1, weights_critic1) + bias_critic1, "output_critic1")

input_critic2 = output_critic1
weights_critic2 = tf.Variable(tf.random_uniform([20, 20]), name="weights_critic2")
bias_critic2 = tf.Variable(tf.random_uniform([20]), name="bias_critic2")
output_critic2 = tf.nn.tanh(tf.matmul(input_critic2, weights_critic2) + bias_critic2, "output_critic2")

input_critic3 = output_critic2
weights_critic3 = tf.Variable(tf.random_uniform([20, 1]), name="weights_critic3")
bias_critic3 = tf.Variable(tf.random_uniform([1]), name="bias_critic3")
output_critic3 = tf.nn.tanh(tf.matmul(input_critic3, weights_critic3) + bias_critic3, "output_critic3")

# train
ass_loss = tf.reduce_sum(output_critic3, name="ass_loss")
real_loss = tf.placeholder(tf.float32, 0, "real_loss")
loss_loss = tf.square(ass_loss - real_loss,"loss_loss")

vars_actor = (weights_actor1, bias_actor1, weights_actor2, bias_actor2)
train_actor = tf.train.AdamOptimizer().minimize(ass_loss, var_list=vars_actor, name="train_actor")

vars_critic = (weights_critic1, bias_critic1, weights_critic2, bias_critic2, weights_critic3, bias_critic3)
train_critic = tf.train.AdamOptimizer().minimize(loss_loss, var_list=vars_critic, name="train_critic")

# session & saver
sess = tf.Session()
sess.run(tf.global_variables_initializer())
saver = tf.train.Saver()
saver.save(sess, "./graph/aiGerms")
print("graph saved.")
