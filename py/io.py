import struct
import sys

# io
rawStdin = sys.stdin.buffer.raw
rawStdout = sys.stdout.buffer


# define
COM_INITIALIZE = 0
COM_FINALIZE = 1
COM_RUN_ACTOR = 2
COM_TRAIN_CRITIC = 3
COM_TRAIN_ACTOR = 4

STU_SUCCEED = 0
STU_FAILED = 1


# read
def read_int():
    read_bytes = rawStdin.read(4)
    return struct.unpack('!i', read_bytes)[0]


def read_float():
    return struct.unpack("!f", rawStdin.read(4))[0]


def read_vector():
    vector = struct.unpack("!2f", rawStdin.read(2 * 4))
    return vector


def read_float_list_raw(size):
    re = [] * size
    for i in range(size):
        re.append(read_float())
    return re


def read_float_list():
    size = read_int()
    return read_float_list_raw(size)


def read_vector_list_raw(size):
    re = [] * size
    for i in range(size):
        re.append(read_vector())
    return re


def read_vector_list():
    size = read_int()
    return read_vector_list_raw(size)


# write
def write_int(value):
    rawStdout.write(struct.pack("!i", value))


def write_float(value):
    rawStdout.write(struct.pack("!f", value))


def write_vector(value):
    rawStdout.write(struct.pack("!2f", *value))


def write_float_list_raw(value_list):
    for value in value_list:
        write_float(value)


def write_float_list(value_list):
    write_int(len(value_list))
    write_float_list_raw(value_list)


def write_vector_list_raw(value_list):
    for value in value_list:
        write_vector(value)


def write_vector_list(value_list):
    write_int(len(value_list))
    write_vector_list_raw(value_list)


def flush_stdout():
    rawStdout.flush()

