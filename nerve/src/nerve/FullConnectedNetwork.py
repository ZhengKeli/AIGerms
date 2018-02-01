import tensorflow as tf


class FullConnectedNetwork:
    def __init__(self, shape, activation, keep=None):
        self.size = shape
        self.keep = keep
        self.activation = activation
        self.weights = [
            tf.Variable(tf.random_normal(shape=[shape[i], shape[i + 1]]))
            for i in range(len(shape) - 1)
        ]
        self.bias = [
            tf.Variable(tf.random_normal(shape=[shape[i + 1]]))
            for i in range(len(shape) - 1)
        ]

    def apply_2d(self, inputs: tf.Tensor, name=None):
        last_output = inputs
        for i in range(len(self.size) - 1):
            layer_input = last_output
            layout_output = self.activation(tf.matmul(layer_input, self.weights[i]) + self.bias[i])

            if self.keep is None:
                layer_output = layout_output
            else:
                layer_output = tf.nn.dropout(layout_output, self.keep)
            last_output = layer_output
        return tf.identity(last_output, name)

    def apply(self, inputs: tf.Tensor, name=None):
        shape = inputs.shape
        if shape is None:
            raise ValueError("The shape of inputs can not be inferred!")

        rank = len(shape)
        if rank < 2:
            raise ValueError("The rank of inputs should be >=2")
        elif rank == 2:
            return self.apply_2d(inputs, name)

        count = shape[-2]
        unstacked_list = tf.unstack(inputs, count, -2)
        flattened = tf.concat(unstacked_list, -2)
        processed = self.apply(flattened, None)
        split_list = tf.split(processed, count, -2)
        stacked = tf.stack(split_list, -2)
        return tf.identity(stacked, name)
