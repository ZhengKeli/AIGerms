import socket

from server.GermsServlet import GermsServlet
from server.GermsSocketIO import GermsSocketIO

socketIO = GermsSocketIO()
servlet = GermsServlet(socketIO)

sock = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
sock.bind(("localhost", 8081))
sock.listen(5)

while True:
    connection, address = sock.accept()
    socketIO.set_socket(connection)
    while servlet.next_command(): pass
    socketIO.close()
