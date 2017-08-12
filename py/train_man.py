import numpy as np
import numpy.random as random

import nerveCore


def get_real_loss(val_feel, val_act):
    val_feel_nutrient = val_feel[:, 0:2]
    return np.sum(np.square(val_act - val_feel_nutrient), 1)


nerveCore.initialize()

train_count = 10 * 1000
train_group_size = 1000
train_id = 1
while True:
    patchSize = 100

    # input feel
    val_feel_nutrient = random.uniform(-1.0, 1.0, [patchSize, 2])
    val_feel_germ = random.uniform(-1.0, 1.0, [patchSize, 2])
    val_feel_wall = random.uniform(-1.0, 1.0, [patchSize, 2])
    val_feel_energy = random.uniform(0.0, 1.0, [patchSize, 1])
    val_feel = np.concatenate([val_feel_nutrient, val_feel_germ, val_feel_wall, val_feel_energy],1)

    # do act
    val_act = nerveCore.run_actor(val_feel)
    val_act += random.uniform(-0.4, 0.4, [patchSize, 2])

    # get real loss
    val_real_loss = get_real_loss(val_feel, val_act)

    # train
    (result_train_critic, _, val_loss_loss) = \
        nerveCore.train_critic(val_feel, val_act, val_real_loss)
    (result_train_actor, val_ass_loss) = nerveCore.train_actor(val_feel)

    if train_id % train_group_size == 0:
        print("[", train_id, "]")
        print("loss_loss =", np.average(val_loss_loss))
        print("ass_loss =", np.average(val_ass_loss))
        print()

    if train_id >= train_count:
        nerveCore.save()
        print("saved graph")
        print()
        break

    train_id += 1

print("train finished")
print("start test loss:")

# input feel
patchSize = 100
val_feel_nutrient = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_germ = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_wall = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_energy = random.uniform(0.0, 1.0, [patchSize, 1])
val_feel = np.concatenate([val_feel_nutrient, val_feel_germ, val_feel_wall, val_feel_energy],1)

# do act
val_act = nerveCore.run_actor(val_feel)

# get real loss
val_real_loss = get_real_loss(val_feel, val_act)

print("test result (patchSize =", patchSize, ") :")
print("real_loss =", np.average(val_real_loss))
print()

nerveCore.finalize(False)
