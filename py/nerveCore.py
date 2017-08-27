import tensorflow as tf

sess = None
graph = None

defaultGraphPath = "./graph"


def initialize(path=defaultGraphPath + "/aiGerms.meta", checkpoint=defaultGraphPath):
    global sess
    sess = tf.Session()
    saver = tf.train.import_meta_graph(path)
    saver.restore(sess, tf.train.latest_checkpoint(checkpoint))

    # graph
    global graph
    graph = sess.graph

    graph.feel = graph.get_tensor_by_name("feel:0")
    graph.act = graph.get_tensor_by_name("act:0")

    graph.ass_loss = graph.get_tensor_by_name("ass_loss:0")
    graph.real_loss = graph.get_tensor_by_name("real_loss:0")
    graph.loss_loss = graph.get_tensor_by_name("loss_loss:0")

    graph.train_actor = graph.get_operation_by_name("train_actor")
    graph.train_critic = graph.get_operation_by_name("train_critic")

    graph.rate_assign_critic = graph.get_tensor_by_name("rate_assign_critic:0")
    graph.assign_critic = graph.get_operation_by_name("assign_critic")


def save():
    global sess, graph
    saver = tf.train.Saver()
    saver.save(sess, "./graph/aiGerms")


def finalize(save_graph=True):
    global sess, graph
    if save_graph: save()
    if sess is not None:
        sess.close()
    sess = None
    graph = None


def test():
    val_act_velocity = sess.run(
        fetches=graph.act_velocity,
        feed_dict={
            graph.feel_nutrient: [[0.0, 0.0]],
            graph.feel_germ: [[0.0, -3.0]],
            graph.feel_energy: [[0.5]]
        }
    )
    print(val_act_velocity)


def run_actor(val_feel):
    return sess.run(
        fetches=graph.act,
        feed_dict={graph.feel: val_feel}
    )


def train_critic(
        val_feel, val_act, val_real_loss,
        val_learning_rate=None):
    feed_dict = {
        graph.feel: val_feel,
        graph.act: val_act,
        graph.real_loss: val_real_loss
    }
    if val_learning_rate is not None:
        feed_dict[graph.learning_rate_critic] = val_learning_rate
    return sess.run(
        fetches=[graph.train_critic, graph.ass_loss, graph.loss_loss],
        feed_dict=feed_dict
    )


def assign_critic(val_rate_assign_critic=None):
    feed_dict = {}
    if val_rate_assign_critic is not None:
        feed_dict[graph.rate_assign_critic] = val_rate_assign_critic
    return sess.run(
        fetches=graph.assign_critic,
        feed_dict=feed_dict)


def train_actor(val_feel, val_learning_rate=None):
    feed_dict = {
        graph.feel: val_feel,
        graph.real_loss: None
    }
    if val_learning_rate is not None:
        feed_dict[graph.learning_rate_actor] = val_learning_rate
    return sess.run(
        fetches=[graph.train_actor, graph.ass_loss],
        feed_dict=feed_dict
    )
