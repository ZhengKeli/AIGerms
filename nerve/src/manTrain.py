import numpy as np

from nerve.GermsNerveCore import *

# nerveCore
defaultGraphPath = "./graph"
defaultGraphName = "aiGerms"
nerveCore = GermsNerveCore(defaultGraphPath, defaultGraphName)


def getRandomPoint(maxR=1.0):
    while True:
        point = np.random.uniform(-maxR, maxR, [2])
        r = np.sqrt(np.sum(np.square(point)))
        if r <= maxR:
            return point, r


for j in range(20):
    train_count = 0
    train_sum_loss_loss = 0
    train_sum_ass_loss = 0
    for i in range(1000):
        val_feel = np.zeros([1, 6], np.float32)
        val_act, val_real_loss = getRandomPoint(1.0)
        (val_train, val_ass_loss, val_loss_loss) = nerveCore.run_train_critic(
            val_feel=val_feel,
            val_act=[val_act],
            val_real_loss=val_real_loss
        )
        (val_train2, val_ass_loss2) = nerveCore.run_train_actor(val_feel)
        train_count += 1
        train_sum_loss_loss += val_loss_loss
        train_sum_ass_loss += val_ass_loss2
    print("train[", j, "]")
    print("count=", train_count)
    print("loss_loss=", train_sum_loss_loss / train_count)
    print("ass_loss=", train_sum_ass_loss / train_count)
    print()

nerveCore.save_graph(defaultGraphPath, defaultGraphName)
