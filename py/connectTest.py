import api

readFloats = api.read_float_list(3)
api.write_float_list(readFloats)
api.rawStdout.flush()
