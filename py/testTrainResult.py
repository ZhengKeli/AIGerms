import numpy as np
import numpy.random as random

import core

core.initialize()


# input feel
patchSize = 100
val_feel_nutrient = random.uniform(-1.0, 1.0, [patchSize, 2])
val_feel_germ = random.uniform(-1.0, 1.0, [patchSize, 2])

# do act
val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ)

# get real loss
val_real_loss = np.sum(np.square(val_act_velocity - (val_feel_nutrient - val_feel_germ)), 1)

print("test result (patchSize =",patchSize,") :")
print("real_loss =", np.average(val_real_loss))
print()


core.finalize(False)
