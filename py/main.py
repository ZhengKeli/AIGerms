import struct
import sys

import numpy as np

import nerveCore

# stdio
rawStdin = sys.stdin.buffer.raw
rawStdout = sys.stdout.buffer
stderr = sys.stderr


# reader
def read_int():
    read_bytes = rawStdin.read(4)
    return struct.unpack('!i', read_bytes)[0]


def read_float():
    return struct.unpack("!f", rawStdin.read(4))[0]


def read_list(decoder: callable):
    size = read_int()
    return [decoder() for x in range(size)]


def read_feel():
    # 2+2+2+1 = 7
    feel = struct.unpack("!7f", rawStdin.read(7 * 4))
    return feel


def read_log():
    # 7+2+1 = 10
    log = struct.unpack("!10f", rawStdin.read(10 * 4))
    return log


# writers
def write_int(value):
    rawStdout.write(struct.pack("!i", value))


def write_float(value):
    rawStdout.write(struct.pack("!f", value))


def write_list(objects: list, encoder: callable):
    write_int(len(objects))
    for item in objects:
        encoder(item)


def write_point2d(value):
    rawStdout.write(struct.pack("!2f", *value))


def flush_stdout():
    rawStdout.flush()


# commands
COM_INITIALIZE = 0
COM_FINALIZE = 1
COM_RUN_ACTOR = 2
COM_TRAIN_CRITIC = 3
COM_TRAIN_ACTOR = 4

# status
STU_SUCCEED = 0
STU_FAILED = 1


# actions
def initialize():
    nerveCore.initialize()
    write_int(STU_SUCCEED)
    flush_stdout()
    stderr.flush()


def finalize():
    save = (read_int() == 0)
    nerveCore.finalize(save)
    write_int(STU_SUCCEED)
    flush_stdout()


def run_actor():
    val_feel = np.array(read_list(read_feel))
    val_act = nerveCore.run_actor(val_feel)

    write_int(STU_SUCCEED)
    write_list(val_act, write_point2d)
    flush_stdout()


def train_critic():
    val_log = np.array(read_list(read_log))
    val_feel = val_log[:, 0:7]
    val_act = val_log[:, 7:9]
    val_real_loss = val_log[:, 9]
    nerveCore.train_critic(val_feel, val_act, val_real_loss)

    write_int(STU_SUCCEED)
    flush_stdout()


def train_actor():
    val_feel = np.array(read_list(read_feel))
    nerveCore.train_actor(val_feel)

    write_int(STU_SUCCEED)
    flush_stdout()


# main cycle
def main():
    while True:
        com = read_int()
        if com == COM_INITIALIZE:
            initialize()
        elif com == COM_FINALIZE:
            finalize()
            break
        elif com == COM_RUN_ACTOR:
            run_actor()
        elif com == COM_TRAIN_CRITIC:
            train_critic()
        elif com == COM_TRAIN_ACTOR:
            train_actor()
        else:
            print("the command", com, "is wrong!")
            break


main()
