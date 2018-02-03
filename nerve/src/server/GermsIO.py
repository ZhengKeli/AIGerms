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

    @abstractmethod
    def close(self):
        pass

    @abstractmethod
    def is_closed(self) -> bool:
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
        return struct.unpack("!6f", self.read(6 * 4))  # 2+2+2 = 6

    def read_act(self):
        return struct.unpack("!2f", self.read(2 * 4))

    def read_log(self):
        val_log_feels = self.read_list(self.read_feel)
        val_log_acts = self.read_list(self.read_act)
        val_real_loss = self.read_float()
        return val_log_feels, val_log_acts, val_real_loss

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
