import struct

import api

readThing = bytes(api.rawStdin.read(4 * 3))
readFloats = struct.unpack('!3f', readThing)
api.rawStdout.write(struct.pack('!f', readFloats[0]))
api.rawStdout.write(struct.pack('!f', readFloats[1]))
api.rawStdout.write(struct.pack('!f', readFloats[2]))
api.rawStdout.flush()
