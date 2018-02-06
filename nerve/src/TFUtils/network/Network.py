from abc import ABCMeta, abstractmethod

import tensorflow as tf


class Network:
    __metaclass__ = ABCMeta

    @abstractmethod
    def apply(self, input_tensor: tf.Tensor, output_name: str = None, name_scope=None) -> tf.Tensor:
        pass
