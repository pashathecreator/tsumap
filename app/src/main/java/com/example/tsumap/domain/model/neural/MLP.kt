package com.example.tsumap.domain.model.neural

import com.example.tsumap.domain.utils.math.MatrixOperations

enum class Activation { RELU, LINEAR }

data class LayerData(
    val weights: Array<FloatArray>,
    val biases: FloatArray,
    val activation: Activation
)

class MLP(private val layers: List<LayerData>) {

    fun forward(input: FloatArray): FloatArray {
        var x = input
        for (layer in layers) {
            x = MatrixOperations.matVecMul(layer.weights, x)
            x = MatrixOperations.addBias(x, layer.biases)
            x = when (layer.activation) {
                Activation.RELU -> MatrixOperations.relu(x)
                Activation.LINEAR -> x
            }
        }
        return x
    }
}