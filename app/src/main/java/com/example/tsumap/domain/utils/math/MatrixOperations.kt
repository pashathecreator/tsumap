package com.example.tsumap.domain.utils.math

import kotlin.math.exp

object MatrixOperations {

    fun matVecMul(W: Array<FloatArray>, x: FloatArray): FloatArray {
        val inSize = W.size
        val outSize = W[0].size

        val out = FloatArray(outSize)
        for (j in 0 until outSize) {
            var sum = 0f
            for (i in 0 until inSize) {
                sum += W[i][j] * x[i]
            }
            out[j] = sum
        }
        return out
    }

    fun addBias(x: FloatArray, b: FloatArray): FloatArray {
        return FloatArray(x.size) { i -> x[i] + b[i] }
    }

    fun relu(x: FloatArray): FloatArray {
        return FloatArray(x.size) { i -> maxOf(0f, x[i]) }
    }

    fun argmax(x: FloatArray): Int {
        var maxIdx = 0
        var maxVal = x[0]
        for (i in 1 until x.size) {
            if (x[i] > maxVal) {
                maxVal = x[i]
                maxIdx = i
            }
        }
        return maxIdx
    }

    fun flattenAndNormalize(image: Array<FloatArray>): FloatArray {
        val out = FloatArray(784)
        for (row in 0 until 28) {
            for (col in 0 until 28) {
                out[row * 28 + col] = image[row][col]
            }
        }
        return out
    }
}