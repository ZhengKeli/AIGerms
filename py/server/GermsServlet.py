import numpy as np

from nerve.GermsNerveCore import GermsNerveCore
from server.GermsIO import GermsIO

# commands
COM_RUN_ACTOR = 1
COM_TRAIN_CRITIC = 2
COM_TRAIN_ACTOR = 3
COM_SAVE_GRAPH = 4
COM_FINISH = 5

# status
STU_SUCCEED = 0
STU_FAILED = 1


class GermsServlet:

    def __init__(self, core: GermsNerveCore = None, io: GermsIO = None):
        self.io = io
        self.core = core
        self.command_map = {
            COM_RUN_ACTOR: self.com_run_actor,
            COM_TRAIN_CRITIC: self.com_train_critic,
            COM_TRAIN_ACTOR: self.com_train_actor,
            COM_SAVE_GRAPH: self.com_save_graph
        }

    # process
    def next_command(self) -> bool:
        com = self.io.read_int()
        if com != COM_FINISH:
            self.command_map[com]()
            return False
        else:
            self.io.close()
            return True

    def next_session(self, io: GermsIO = None):
        if io is not None:
            self.io = io
        while not self.next_command():
            pass

    # commands
    def com_run_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        val_act = self.core.run_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.write_list(val_act, self.io.write_act)
        self.io.flush()

    def com_train_critic(self):
        val_log = np.array(self.io.read_list(self.io.read_log))
        val_feel = val_log[:, 0:6]
        val_act = val_log[:, 6:8]
        val_real_loss = val_log[:, 8]
        self.core.run_train_critic(val_feel, val_act, val_real_loss)

        self.io.write_int(STU_SUCCEED)
        self.io.flush()

    def com_train_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        self.core.run_train_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.flush()

    def com_save_graph(self):
        self.core.save_graph()
        self.io.write_int(STU_SUCCEED)
        self.io.flush()
