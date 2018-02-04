import socket

from nerve.GermsNerveCore import GermsNerveCore
from server.GermsServlet import GermsServlet
from server.GermsSocketIO import GermsSocketIO

core = GermsNerveCore("../graph/core", "core")
servlet = GermsServlet(core)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("localhost", 8081))
sock.listen(5)

print("Server is ready")
while True:
    try:
        session_socket, address = sock.accept()
        print("connected to ", address[0], ":", address[1])
        session_io = GermsSocketIO(session_socket)
        servlet.next_session(session_io)
        session_io.close()
        print("disconnected with ", address[0], ":", address[1])
    except ConnectionResetError:
        print("connection lost!")
