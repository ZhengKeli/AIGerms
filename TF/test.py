import random

import tensorflow as tf

in_x = tf.placeholder(tf.float32, name="in_x")
k1_x = tf.Variable(random.uniform(-1.0, 1.0), "k1_x")
b1 = tf.Variable(random.uniform(-1.0, 1.0), "b1")
out_xp = in_x * k1_x + b1

real_dis = tf.placeholder(tf.float32, name="real_loss")
k2_xp = tf.Variable(random.uniform(-1.0, 1.0), "k2_xp")
k2_x = tf.Variable(random.uniform(-1.0, 1.0), "k2_x")
b2 = tf.Variable(random.uniform(-1.0, 1.0), "b2")

ass_dis = out_xp * k2_xp + in_x * k2_x + b2
ass_loss = tf.square(ass_dis)
dis_loss = tf.square(ass_dis - real_dis)

group1 = [k1_x, b1]
train1 = tf.train.GradientDescentOptimizer(0.1).minimize(ass_loss, var_list=group1)

group2 = [k2_x, k2_xp, b2]
train2 = tf.train.GradientDescentOptimizer(0.1).minimize(dis_loss, var_list=group2)

sess = tf.Session()
sess.run(tf.global_variables_initializer())

i = 0
group_size = 500
sum_real_loss = 0.0
sum_ass_loss = 0.0
sum_dis_loss = 0.0

while True:
    i += 1
    val_in_x = random.uniform(-1, 1)
    val_out_xp = sess.run(out_xp, feed_dict={in_x: val_in_x, real_dis: None})
    val_real_dis = val_in_x - val_out_xp   #+ random.uniform(-1.0, 1.0)*0.1
    _, val_dis_loss = sess.run([train2, dis_loss], feed_dict={in_x: val_in_x, out_xp: val_out_xp, real_dis: val_real_dis})
    _, val_ass_loss = sess.run([train1, ass_loss], feed_dict={in_x: val_in_x, real_dis: val_real_dis})

    sum_real_loss += val_real_dis ** 2
    sum_ass_loss += val_ass_loss
    sum_dis_loss += val_dis_loss

    if i % group_size == 0:
        ave_real_loss = sum_real_loss / group_size
        sum_real_loss = 0.0

        ave_dis_loss = sum_dis_loss / group_size
        sum_dis_loss = 0.0

        ave_ass_loss = sum_ass_loss / group_size
        sum_ass_loss = 0.0

        print("[", i, "]")
        print("real_loss:", ave_real_loss)
        print("ass_loss:", ave_ass_loss)
        print("dis_loss:", ave_dis_loss)
        print("k1_x", sess.run([k1_x, b1]))
        if ave_real_loss < 0.001: break
