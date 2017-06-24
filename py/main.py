import py.api as api
from py.api import Command

sess = None

while True:
    com = api.read_int()
    if com == Command.INITIALIZE:
        break
    # todo
    elif com == Command.FINALIZE:
        if sess is not None:
            sess.close()
            sess = None
            break
