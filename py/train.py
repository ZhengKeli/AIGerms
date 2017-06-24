import tensorflow as tf

sess = tf.Session()
saver = tf.train.import_meta_graph("./graph/aiGerms.meta")
saver.restore(sess, tf.train.latest_checkpoint('./graph'))

# graph
graph = sess.graph

feel_nutrient = graph.get_tensor_by_name("feel_nutrient:0")
feel_germ = graph.get_tensor_by_name("feel_germ:0")

act_velocity = graph.get_tensor_by_name("act_velocity:0")

ass_loss = graph.get_tensor_by_name("ass_loss:0")
real_loss = graph.get_tensor_by_name("real_loss:0")
loss_loss = graph.get_tensor_by_name("loss_loss:0")

train_actor = graph.get_operation_by_name("train_actor")
train_critic = graph.get_operation_by_name("train_critic")

# test
val_act_velocity = sess.run(act_velocity,{feel_nutrient:[0.0,0.0],feel_germ:[0.0,-3.0]})
print(val_act_velocity)


