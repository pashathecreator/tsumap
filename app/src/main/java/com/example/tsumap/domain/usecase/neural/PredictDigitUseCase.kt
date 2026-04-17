package com.example.tsumap.domain.usecase.neural

import com.example.tsumap.domain.model.neural.MLP
import com.example.tsumap.domain.utils.math.MatrixOperations

class PredictDigitUseCase(private val mlp: MLP) {

    fun invoke(image: Array<FloatArray>): Int {
        val input = MatrixOperations.flattenAndNormalize(centerImage(image))

        val logits = mlp.forward(input)
        return MatrixOperations.argmax(logits)
    }
    private fun centerImage(image: Array<FloatArray>): Array<FloatArray> {
        var totalMass = 0f; var massRow = 0f; var massCol = 0f
        for (r in 0 until 28) for (c in 0 until 28) {
            totalMass += image[r][c]
            massRow += r * image[r][c]
            massCol += c * image[r][c]
        }
        if (totalMass == 0f) return image
        val shiftRow = 14 - (massRow / totalMass).toInt()
        val shiftCol = 14 - (massCol / totalMass).toInt()
        val result = Array(28) { FloatArray(28) }
        for (r in 0 until 28) for (c in 0 until     28) {
            val nr = r + shiftRow; val nc = c + shiftCol
            if (nr in 0..27 && nc in 0..27) result[nr][nc] = image[r][c]
        }
        return result
    }
}