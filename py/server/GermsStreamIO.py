from server.GermsIO import GermsIO


class GermsStreamIO(GermsIO):

    def __init__(self, in_stream=None, out_stream=None):
        self.in_stream = in_stream
        self.out_stream = out_stream

    def read(self, size: int) -> bytes:
        return self.in_stream.read(size)

    def write(self, data: bytes):
        self.out_stream.write(data)

    def flush(self):
        self.out_stream.flush()
