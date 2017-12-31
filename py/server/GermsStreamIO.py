from server.GermsIO import GermsIO


class GermsStreamIO(GermsIO):

    def __init__(self, in_stream, out_stream):
        self.in_stream = in_stream
        self.out_stream = out_stream
        self.closed = False

    def read(self, size: int) -> bytes:
        return self.in_stream.read(size)

    def write(self, data: bytes):
        self.out_stream.write(data)

    def flush(self):
        self.out_stream.flush()

    def close(self):
        self.in_stream.close()
        self.out_stream.close()
        self.closed = True

    def is_closed(self) -> bool:
        return self.closed
