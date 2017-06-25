import api

readFloats = api.read_float_list()
api.write_float_list(readFloats)
api.rawStdout.flush()
