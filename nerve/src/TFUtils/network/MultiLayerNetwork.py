from abc import ABCMeta, abstractmethod

import tensorflow as tf

from TFUtils.network.Network import Network


class MultiLayerNetwork(Network):
    __metaclass__ = ABCMeta

    def __init__(self, size):
        self.size = size

    def apply(self, input_tensor: tf.Tensor, output_name: str = None, name_scope=None) -> tf.Tensor:
        if name_scope is not None:
            with tf.name_scope(name_scope):
                return self.apply(input_tensor, output_name, None)

        if output_name is not None:
            return tf.identity(self.apply(input_tensor, None, name_scope))

        return self.on_apply(input_tensor)

    def on_apply(self, input_tensor: tf.Tensor) -> tf.Tensor:
        last_output = input_tensor
        for i in range(len(self.size) - 1):
            last_output = self.layer(i, last_output)
        return last_output

    @abstractmethod
    def layer(self, index: int, input_tensor: tf.Tensor):
        pass
