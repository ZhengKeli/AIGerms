import tensorflow as tf

sess = None
graph = None


def initialize():
    global sess
    sess = tf.Session()
    saver = tf.train.import_meta_graph("./graph/aiGerms.meta")
    saver.restore(sess, tf.train.latest_checkpoint('./graph'))

    # graph
    global graph
    graph = sess.graph

    graph.feel_nutrient = graph.get_tensor_by_name("feel_nutrient:0")
    graph.feel_germ = graph.get_tensor_by_name("feel_germ:0")

    graph.act_velocity = graph.get_tensor_by_name("act_velocity:0")

    graph.ass_loss = graph.get_tensor_by_name("ass_loss:0")
    graph.real_loss = graph.get_tensor_by_name("real_loss:0")
    graph.loss_loss = graph.get_tensor_by_name("loss_loss:0")

    graph.train_actor = graph.get_operation_by_name("train_actor")
    graph.train_critic = graph.get_operation_by_name("train_critic")


def finalize():
    global sess
    global graph
    if sess is not None:
        sess.close()
    sess = None
    graph = None


def test():
    val_act_velocity = sess.run(graph.act_velocity, {graph.feel_nutrient: [0.0, 0.0], graph.feel_germ: [0.0, -3.0]})
    print(val_act_velocity)


def run_actor(val_feel_nutrient, val_feel_germ):
    return sess.run(
        fetches=graph.act_velocity,
        feed_dict={
            graph.feel_nutrient: val_feel_nutrient,
            graph.feel_germ: val_feel_germ
        }
    )


def train_critic(val_feel_nutrient, val_feel_germ, val_act_velocity, val_real_loss):
    return sess.run(
        fetches=graph.train_critic,
        feed_dict={
            graph.act_velocity: val_act_velocity,
            graph.feel_nutrient: val_feel_nutrient,
            graph.feel_germ: val_feel_germ,
            graph.real_loss: val_real_loss
        }
    )


def train_actor(val_feel_nutrient, val_feel_germ):
    return sess.run(
        fetches=graph.train_actor,
        feed_dict={
            graph.feel_nutrient: val_feel_nutrient,
            graph.feel_germ: val_feel_germ,
            graph.real_loss: None
        }
    )
