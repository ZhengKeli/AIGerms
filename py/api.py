import struct
import sys

from enum import Enum, unique

# io
rawStdin = sys.stdin.buffer.raw
rawStdout = sys.stdout.buffer


# define
@unique
class Command(Enum):
    INITIALIZE = 0
    FINALIZE = 1

    RUN_ACTOR = 2
    RUN_ACTOR_DISTURB = 3

    TRAIN_CRITIC = 4
    TRAIN_CRITIC_INHERIT = 5


@unique
class Status(Enum):
    SUCCEED = 0
    FAILED = 1


# read
def read_int():
    return struct.unpack('!i', rawStdin.read(4))[0]


def read_float():
    return struct.unpack("!f", rawStdin.read(4))[0]


def read_vector():
    vector = struct.unpack("!2f", rawStdin.read(2 * 4))
    return vector


def read_float_list(size):
    re = [] * size
    for i in range(size):
        re.append(read_float())
    return re


def read_vector_list(size):
    re = [] * size
    for i in range(size):
        re.append(read_vector())
    return re


# write
def write_int(value):
    rawStdout.write(struct.pack("!i", value))


def write_float(value):
    rawStdout.write(struct.pack("!f", value))


def write_vector(value):
    rawStdout.write(struct.pack("!2f", *value))


def write_float_list(value_list):
    for value in value_list:
        write_float(value)


def write_vector_list(value_list):
    for value in value_list:
        write_vector(value)


def flush_stdout():
    rawStdout.flush()