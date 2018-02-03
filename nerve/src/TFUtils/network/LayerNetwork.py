from abc import ABCMeta, abstractmethod

import tensorflow as tf

from TFUtils.network.Network import Network


class LayerNetwork(Network):
    __metaclass__ = ABCMeta

    def __init__(self, size):
        self.size = size

    def apply(self, input_tensor: tf.Tensor, output_name: str = None) -> tf.Tensor:
        last_output = input_tensor
        for i in range(len(self.size) - 1):
            last_output = self.layer(i, last_output)
        return tf.identity(last_output, output_name)

    @abstractmethod
    def layer(self, index: int, input_tensor: tf.Tensor):
        pass
