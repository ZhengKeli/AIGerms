import struct
from abc import ABCMeta, abstractmethod


class GermsIO:
    __metaclass__ = ABCMeta

    # abstracts
    @abstractmethod
    def read(self, size: int) -> bytes:
        pass

    @abstractmethod
    def write(self, data: bytes):
        pass

    @abstractmethod
    def flush(self):
        pass

    # reader
    def read_int(self):
        read_bytes = self.read(4)
        return struct.unpack('!i', read_bytes)[0]

    def read_float(self):
        return struct.unpack("!f", self.read(4))[0]

    def read_list(self, decoder: callable):
        size = self.read_int()
        return [decoder() for x in range(size)]

    def read_feel(self):
        # 2+2+2 = 6
        feel = struct.unpack("!6f", self.read(6 * 4))
        return feel

    def read_log(self):
        # 6+2+1 = 9
        log = struct.unpack("!9f", self.read(9 * 4))
        return log

    # writers
    def write_int(self, value):
        self.write(struct.pack("!i", value))

    def write_float(self, value):
        self.write(struct.pack("!f", value))

    def write_list(self, objects: list, encoder: callable):
        self.write_int(len(objects))
        for item in objects:
            encoder(item)

    def write_act(self, value):
        self.write(struct.pack("!2f", *value))
