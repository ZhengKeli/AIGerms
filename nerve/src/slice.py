import matplotlib.pyplot as plt
import numpy as np

from nerve.GermsNerveCore import GermsNerveCore

core = GermsNerveCore("../graph/core", "core")

val_feel_nutrients = np.array([(x, 0.0) for x in np.arange(-1.0, 1.0, 0.01)])
val_feel_germs = np.array([(0, 0) for _ in np.arange(-1.0, 1.0, 0.01)])
val_feel_walls = np.array([(0, 0) for _ in np.arange(-1.0, 1.0, 0.01)])
val_feel = np.concatenate([val_feel_nutrients, val_feel_germs, val_feel_walls], -1)

val_act = core.run_actor(val_feel)

plt.plot(val_feel_nutrients[:, 0], val_act[:, 0])
plt.show()
