import tensorflow as tf

from TFUtils.network.MultiLayerNetwork import MultiLayerNetwork


class MultiDenseNetwork(MultiLayerNetwork):

    def __init__(self, size, activation, weights_name_scope="MultiDenseWeights"):
        super().__init__(size)
        self.activation = activation
        self.name_scope = weights_name_scope
        with tf.name_scope(weights_name_scope):
            self.weights = [
                tf.Variable(tf.random_normal(shape=[size[i], size[i + 1]]))
                for i in range(len(size) - 1)
            ]
            self.bias = [
                tf.Variable(tf.random_normal(shape=[size[i + 1]]))
                for i in range(len(size) - 1)
            ]

    def layer(self, index: int, input_tensor: tf.Tensor):
        layer_output = tf.matmul(input_tensor, self.weights[index]) + self.bias[index]
        layer_output = self.activation(layer_output)
        return layer_output

    def on_apply(self, input_tensor: tf.Tensor) -> tf.Tensor:
        shape = input_tensor.shape
        if shape is None:
            raise ValueError("The shape of inputs can not be inferred!")

        rank = len(shape)
        if rank < 2:
            raise ValueError("The rank of inputs should be >=2")
        if rank == 2:
            return super().on_apply(input_tensor)

        count = shape[-2]
        unstacked_list = tf.unstack(input_tensor, count, -2)
        flattened = tf.concat(unstacked_list, -2)
        processed = self.apply(flattened, None)
        split_list = tf.split(processed, count, -2)
        stacked = tf.stack(split_list, -2)
        return stacked
