import socket

from nerve.GermsNerveCore import GermsNerveCore
from server.GermsServlet import GermsServlet
from server.GermsSocketIO import GermsSocketIO

core = GermsNerveCore("./graph/core1", "core")
servlet = GermsServlet(core)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("localhost", 8081))
sock.listen(5)

while True:
    session_socket, address = sock.accept()
    session_io = GermsSocketIO(session_socket)
    servlet.next_session(session_io)
    session_io.close()
