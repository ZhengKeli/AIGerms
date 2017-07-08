import tensorflow as tf

# feeling network
feel_nutrient = tf.placeholder(tf.float32, name="feel_nutrient")  # [-1,2]
feel_germ = tf.placeholder(tf.float32, name="feel_germ")  # [-1,2]
feel_energy = tf.placeholder(tf.float32, name="feel_energy")  # [-1]
feel_energy = tf.stack([feel_energy], 1)  # [-1,1]

# actor network
input_actor1 = tf.concat([feel_nutrient, feel_germ, feel_energy], 1, "input_actor1")  # [-1,5]
weights_actor1 = tf.Variable(tf.random_uniform([5, 10], -1.0, 1.0), name="weights_actor1")
bias_actor1 = tf.Variable(tf.random_uniform([10], -1.0, 1.0), name="bias_actor1")
output_actor1 = tf.nn.tanh(tf.matmul(input_actor1, weights_actor1) + bias_actor1, "output_actor1")  # [-1,10]

input_actor2 = output_actor1  # [-1,10]
weights_actor2 = tf.Variable(tf.random_uniform([10, 2], -1.0, 1.0), name="weights_actor2")  # [-1,2]
bias_actor2 = tf.Variable(tf.random_uniform([2], -1.0, 1.0), name="bias_actor2")
output_actor2 = tf.nn.tanh(tf.matmul(input_actor2, weights_actor2) + bias_actor2, "output_actor2")  # [-1,2]

act_velocity = tf.multiply(output_actor2, 1.0, "act_velocity")  # [-1,2]

# critic network
input_critic1 = tf.concat([feel_nutrient, feel_germ, feel_energy, act_velocity], 1, "input_critic1")  # [-1,7]
weights_critic1 = tf.Variable(tf.random_uniform([7, 20], -1.0, 1.0), name="weights_critic1")
bias_critic1 = tf.Variable(tf.random_uniform([20], -1.0, 1.0), name="bias_critic1")
output_critic1 = tf.nn.tanh(tf.matmul(input_critic1, weights_critic1) + bias_critic1, "output_critic1")  # [-1,20]

input_critic2 = output_critic1
weights_critic2 = tf.Variable(tf.random_uniform([20, 20], -1.0, 1.0), name="weights_critic2")
bias_critic2 = tf.Variable(tf.random_uniform([20], -1.0, 1.0), name="bias_critic2")
output_critic2 = tf.nn.tanh(tf.matmul(input_critic2, weights_critic2) + bias_critic2, "output_critic2")  # [-1,20]

input_critic3 = output_critic2
weights_critic3 = tf.Variable(tf.random_uniform([20, 1], -1.0, 1.0), name="weights_critic3")
bias_critic3 = tf.Variable(tf.random_uniform([1], -1.0, 1.0), name="bias_critic3")
output_critic3 = tf.nn.tanh(tf.matmul(input_critic3, weights_critic3) + bias_critic3, "output_critic3")  # [-1,1]

# train
ass_loss = tf.multiply(tf.reduce_sum(output_critic3, -1), 10.0, name="ass_loss")  # [-1]
real_loss = tf.placeholder(tf.float32, name="real_loss")  # [-1]
loss_loss = tf.square(ass_loss - real_loss, name="loss_loss")  # [-1]

vars_actor = (weights_actor1, bias_actor1, weights_actor2, bias_actor2)
learning_rate_actor = tf.Variable(0.01, name="learning_rate_actor")
train_actor = tf.train.AdamOptimizer(learning_rate_actor) \
    .minimize(tf.reduce_sum(ass_loss), var_list=vars_actor, name="train_actor")

vars_critic = (weights_critic1, bias_critic1, weights_critic2, bias_critic2, weights_critic3, bias_critic3)
learning_rate_critic = tf.Variable(0.01, name="learning_rate_critic")
train_critic = tf.train.AdamOptimizer(learning_rate_critic) \
    .minimize(tf.reduce_sum(loss_loss), var_list=vars_critic, name="train_critic")

# session
sess = tf.Session()
sess.run(tf.global_variables_initializer())
saver = tf.train.Saver()
saver.save(sess, "./graph/aiGerms")
print("graph saved.")
