import numpy as np

from nerve.GermsNerveCore import GermsNerveCore

core = GermsNerveCore("../graph/core", "core")

for j in range(16):
    losses = []
    for i in range(1024):
        val_feel = np.random.uniform(-1.0, 1.0, [10, 6])
        val_man_act = val_feel[:, 0:2] - 0.01 * val_feel[:, 2:4] - 0.03 * val_feel[:, 4:6]

        _, val_ave_man_loss = core.sess.run(
            fetches=[
                core.man_train_actor,
                core.ave_man_loss
            ],
            feed_dict={
                core.feel: val_feel,
                core.man_act: val_man_act,
            }
        )
        losses.append(val_ave_man_loss)
    print("ave_man_loss =", np.average(losses))
    core.save_graph()
