import struct
import sys

import numpy as np

import core

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
    # 2+2+1 = 5
    feel = struct.unpack("!5f", rawStdin.read(5 * 4))
    return feel


def read_log():
    # 5+2+1 = 8
    log = struct.unpack("!8f", rawStdin.read(8 * 4))
    return log


# writers
def write_int(value):
    rawStdout.write(struct.pack("!i", value))


def write_float(value):
    rawStdout.write(struct.pack("!f", value))


def write_list(objects: list, encoder: callable(object)):
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
    core.initialize()
    write_int(STU_SUCCEED)
    flush_stdout()
    stderr.flush()


def finalize():
    core.finalize()
    write_int(STU_SUCCEED)
    flush_stdout()


def run_actor():
    val_feel = np.array(read_list(read_feel))

    val_feel_nutrient = val_feel[:, 0:2]
    val_feel_germ = val_feel[:, 2:4]
    val_feel_energy = val_feel[:, 4]

    val_act_velocity = core.run_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)

    write_int(STU_SUCCEED)
    write_list(val_act_velocity, write_point2d)
    flush_stdout()


def train_critic():
    val_log = np.array(read_list(read_log))

    val_feel_nutrient = val_log[:, 0:2]
    val_feel_germ = val_log[:, 2:4]
    val_feel_energy = val_log[:, 4]
    val_act_velocity = val_log[:, 5:7]
    val_real_loss = val_log[:, 7]
    core.train_critic(val_feel_nutrient, val_feel_germ, val_feel_energy, val_act_velocity, val_real_loss)

    write_int(STU_SUCCEED)
    flush_stdout()


def train_actor():
    val_feel = np.array(read_list(read_feel))

    val_feel_nutrient = val_feel[:, 0:2]
    val_feel_germ = val_feel[:, 2:4]
    val_feel_energy = val_feel[:, 4]
    core.train_actor(val_feel_nutrient, val_feel_germ, val_feel_energy)

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
