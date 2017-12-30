import numpy as np

from nerve.GermsNerveCore import GermsNerveCore
from server.GermsIO import GermsIO

# nerveCore
defaultGraphPath = "./graph"
defaultGraphName = "aiGerms"

# commands
COM_INITIALIZE = 0
COM_FINALIZE = 1
COM_RUN_ACTOR = 2
COM_TRAIN_CRITIC = 3
COM_TRAIN_ACTOR = 4

# status
STU_SUCCEED = 0
STU_FAILED = 1


class GermsServlet:

    def __init__(self, io: GermsIO):
        self.io = io
        self.nerveCore = None

    def next_command(self) -> bool:
        com = self.io.read_int()
        if com == COM_INITIALIZE:
            self.initialize()
            return True
        elif com == COM_FINALIZE:
            self.finalize()
            return False
        elif com == COM_RUN_ACTOR:
            self.run_actor()
            return True
        elif com == COM_TRAIN_CRITIC:
            self.train_critic()
            return True
        elif com == COM_TRAIN_ACTOR:
            self.train_actor()
            return True
        else:
            print("the command", com, "is wrong!")
            return False

    # actions
    def initialize(self):
        self.nerveCore = GermsNerveCore(defaultGraphPath, defaultGraphName)
        self.io.write_int(STU_SUCCEED)
        self.io.flush()

    def finalize(self):
        save = (self.io.read_int() == 0)

        if save:
            self.nerveCore.save_graph(defaultGraphPath, defaultGraphName)
        self.nerveCore.sess.close()

        self.io.write_int(STU_SUCCEED)
        self.io.flush()

    def run_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        val_act = self.nerveCore.run_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.write_list(val_act, self.io.write_act)
        self.io.flush()

    def train_critic(self):
        val_log = np.array(self.io.read_list(self.io.read_log))
        val_feel = val_log[:, 0:6]
        val_act = val_log[:, 6:8]
        val_real_loss = val_log[:, 8]
        self.nerveCore.run_train_critic(val_feel, val_act, val_real_loss)

        self.io.write_int(STU_SUCCEED)
        self.io.flush()

    def train_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        self.nerveCore.run_train_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.flush()
