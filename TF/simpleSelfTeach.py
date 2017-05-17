import random

import tensorflow as tf

xi = tf.placeholder(tf.float32, name="xi")
k1 = tf.Variable(tf.random_uniform([1, 5], -1.0, 1.0), "k1")
b1 = tf.Variable(tf.random_uniform([5], -1.0, 1.0), "b1")
xp = tf.reduce_mean(tf.tanh(tf.matmul([[xi]], k1) + b1)) * 4.0

real_loss = tf.placeholder(tf.float32, name="real_loss")
in2 = tf.stack([[xi, xp]])
k2 = tf.Variable(tf.random_uniform([2, 10], -1.0, 1.0), "k2")
b2 = tf.Variable(tf.random_uniform([1, 10], -1.0, 1.0), "b2")

ass_loss = tf.reduce_mean(tf.tanh(tf.matmul(in2, k2) + b2)) * 4.0
loss_loss = tf.square(ass_loss - real_loss)

group1 = [k1, b1]
train1 = tf.train.GradientDescentOptimizer(0.1).minimize(ass_loss, var_list=group1)

group2 = [k2, b2]
train2 = tf.train.GradientDescentOptimizer(0.1).minimize(loss_loss, var_list=group2)

sess = tf.Session()
sess.run(tf.global_variables_initializer())

func_real_loss = lambda i, p: ((i - p) ** 2) * 0.5

preTrainTimes = 1000 # preTrain 非常有用！
trainCount = 0
group_size = 500
sum_real_loss = 0.0
sum_ass_loss = 0.0
sum_loss_loss = 0.0

for preTrainCount in range(preTrainTimes):
    val_xi = random.uniform(-1.0, 1.0)
    val_xp = random.uniform(-2.0, 2.0)
    val_real_loss = func_real_loss(val_xi, val_xp)
    sess.run(train2, feed_dict={xi: val_xi, xp: val_xp, real_loss: val_real_loss})

print("pre trained loss ass ", preTrainTimes, " times.")

while True:
    trainCount += 1
    val_xi = random.uniform(-1, 1)
    val_xp = sess.run(xp, feed_dict={xi: val_xi})
    val_real_loss = func_real_loss(val_xi, val_xp)
    _, val_dis_loss = sess.run([train2, loss_loss], feed_dict={xi: val_xi, xp: val_xp, real_loss: val_real_loss})
    _, val_ass_loss = sess.run([train1, ass_loss], feed_dict={xi: val_xi, real_loss: val_real_loss})

    sum_real_loss += val_real_loss
    sum_ass_loss += val_ass_loss
    sum_loss_loss += val_dis_loss

    if trainCount % group_size == 0:
        ave_real_loss = sum_real_loss / group_size
        sum_real_loss = 0.0

        ave_ass_loss = sum_ass_loss / group_size
        sum_ass_loss = 0.0

        ave_loss_loss = sum_loss_loss / group_size
        sum_loss_loss = 0.0

        print("[", trainCount, "]")
        print("real_loss:", ave_real_loss)
        print("ass_loss:", ave_ass_loss)
        print("loss_loss:", ave_loss_loss)
        if ave_real_loss < 0.05:
            break
