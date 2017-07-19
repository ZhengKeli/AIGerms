import numpy as np
import numpy.random as random

import core


def get_real_loss(val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity):
    return np.sum(np.square(val_act_velocity - (val_feel_nutrient - val_feel_germ)), 1)


core.initialize()
train_id = 1
while True:
    patchSize = 100

    # input feel
    val_feel_nutrient = random.uniform(-1.0, 1.0, [patchSize, 2])
    val_feel_germ = random.uniform(-1.0, 1.0, [patchSize, 2])
    val_feel_energy = random.uniform(0.0, 1.0, [patchSize])

    # do act
    val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)
    val_act_velocity += random.uniform(-0.4, 0.4, [patchSize, 2])

    # get real loss
    val_real_loss = get_real_loss(val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity)

    # train
    (result_train_critic, _, val_loss_loss) = core.train_critic(
        val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity, val_real_loss)
    (result_train_actor, val_ass_loss) = core.train_actor(val_feel_nutrient, val_feel_germ, val_feel_energy, 0.01)

    if train_id % 1000 == 0:
        print("[", train_id, "]")
        print("loss_loss =", np.average(val_loss_loss))
        print("ass_loss =", np.average(val_ass_loss))
        print()
        if train_id % (10 * 1000) == 0:
            core.save()
            print("saved graph")
            print()
            if train_id >= 50 * 1000:
                break

    train_id += 1

print("train finished")
print("start test loss:")

# input feel
patchSize = 100
val_feel_nutrient = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_germ = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_energy = random.uniform(0.0, 1.0, [patchSize])

# do act
val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)

# get real loss
val_real_loss = get_real_loss(val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity)

print("test result (patchSize =", patchSize, ") :")
print("real_loss =", np.average(val_real_loss))
print()

core.finalize(False)
