import sys

from server.GermsServlet import GermsServlet
from server.GermsStreamIO import GermsStreamIO

# stdio
rawStdin = sys.stdin.buffer.raw
rawStdout = sys.stdout.buffer
stderr = sys.stderr

nerveCoreSession = GermsServlet(GermsStreamIO(rawStdin, rawStdout))
