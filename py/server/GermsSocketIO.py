import socket

from server.GermsStreamIO import GermsStreamIO


class GermsSocketIO(GermsStreamIO):
    def __init__(self, sock: socket.socket = None):
        super().__init__()
        self.socket = None
        self.set_socket(sock)

    def set_socket(self, sock: socket.socket):
        self.socket = sock
        if sock is not None:
            self.in_stream = sock.makefile('rb')
            self.out_stream = sock.makefile('wb')

    def close(self):
        self.in_stream.close()
        self.out_stream.close()
        self.socket.close()
