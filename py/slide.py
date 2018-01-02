import matplotlib.pyplot as plt
import numpy as np

from nerve.GermsNerveCore import GermsNerveCore

core = GermsNerveCore("../graph/core", "core")

val_feel_nutrients = np.array([(0.5, 0.5) for x in np.arange(-1.0, 1.0, 0.01)])
val_feel_germs = np.array([(0, 0) for x in np.arange(-1.0, 1.0, 0.01)])
val_feel_walls = np.array([(0, 0) for x in np.arange(-1.0, 1.0, 0.01)])
val_feel = np.concatenate([val_feel_nutrients, val_feel_germs, val_feel_walls], -1)

val_act = np.array([(x, 0) for x in np.arange(-1.0, 1.0, 0.01)])

val_ass_loss = core.run_critic(val_feel, val_act)

plt.plot(val_act[:, 0], val_ass_loss)
plt.show()
