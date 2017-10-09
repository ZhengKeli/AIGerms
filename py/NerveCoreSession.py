import struct

import numpy as np

from TFNerveCore import TFNerveCore

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


class NerveCoreSession:

    def __init__(self, io):
        self.io = io
        self.nerveCore = None

        while True:
            com = self.io.read_int()
            if com == COM_INITIALIZE:
                self.initialize()
            elif com == COM_FINALIZE:
                self.finalize()
                break
            elif com == COM_RUN_ACTOR:
                self.run_actor()
            elif com == COM_TRAIN_CRITIC:
                self.train_critic()
            elif com == COM_TRAIN_ACTOR:
                self.train_actor()
            else:
                print("the command", com, "is wrong!")
                break

    # actions
    def initialize(self):
        self.nerveCore = TFNerveCore(defaultGraphPath, defaultGraphName)
        self.io.write_int(STU_SUCCEED)
        self.io.flush_out()
        self.io.flush_err()

    def finalize(self):
        save = (self.io.read_int() == 0)

        if save:
            self.nerveCore.save_graph(defaultGraphPath, defaultGraphName)
        self.nerveCore.sess.close()

        self.io.write_int(STU_SUCCEED)
        self.io.flush_out()

    def run_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        val_act = self.nerveCore.run_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.write_list(val_act, self.io.write_point2d)
        self.io.flush_out()

    def train_critic(self):
        val_log = np.array(self.io.read_list(self.io.read_log))
        val_feel = val_log[:, 0:7]
        val_act = val_log[:, 7:9]
        val_real_loss = val_log[:, 9]
        self.nerveCore.run_train_critic(val_feel, val_act, val_real_loss)

        self.io.write_int(STU_SUCCEED)
        self.io.flush_out()

    def train_actor(self):
        val_feel = np.array(self.io.read_list(self.io.read_feel))
        self.nerveCore.run_train_actor(val_feel)

        self.io.write_int(STU_SUCCEED)
        self.io.flush_out()


class NerveCoreIOWrapper:
    def __init__(self, in_stream, out_stream, err_stream=None):
        self.in_stream = in_stream
        self.out_stream = out_stream
        self.err_stream = err_stream

    # reader
    def read_int(self):
        read_bytes = self.in_stream.read(4)
        return struct.unpack('!i', read_bytes)[0]

    def read_float(self):
        return struct.unpack("!f", self.in_stream.read(4))[0]

    def read_list(self, decoder: callable):
        size = self.read_int()
        return [decoder() for x in range(size)]

    def read_feel(self):
        # 2+2+2+1 = 7
        feel = struct.unpack("!7f", self.in_stream.read(7 * 4))
        return feel

    def read_log(self):
        # 7+2+1 = 10
        log = struct.unpack("!10f", self.in_stream.read(10 * 4))
        return log

    # writers
    def write_int(self, value):
        self.out_stream.write(struct.pack("!i", value))

    def write_float(self, value):
        self.out_stream.write(struct.pack("!f", value))

    def write_list(self, objects: list, encoder: callable):
        self.write_int(len(objects))
        for item in objects:
            encoder(item)

    def write_point2d(self, value):
        self.out_stream.write(struct.pack("!2f", *value))

    def flush_out(self):
        self.out_stream.flush()

    def flush_err(self):
        if self.err_stream is not None:
            self.err_stream.flush()
