import socket

from server.GermsStreamIO import GermsStreamIO


class GermsSocketIO(GermsStreamIO):
    def __init__(self, sock: socket.socket):
        super().__init__(sock.makefile('rb'), sock.makefile('wb'))
        self.socket = sock

    def close(self):
        super().close()
        self.socket.close()

    def is_closed(self) -> bool:
        return super().is_closed()
