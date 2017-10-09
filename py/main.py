import sys

from NerveCoreSession import NerveCoreIOWrapper
from NerveCoreSession import NerveCoreSession

# stdio
rawStdin = sys.stdin.buffer.raw
rawStdout = sys.stdout.buffer
stderr = sys.stderr

nerveCoreSession = NerveCoreSession(NerveCoreIOWrapper(rawStdin, rawStdout, None))
