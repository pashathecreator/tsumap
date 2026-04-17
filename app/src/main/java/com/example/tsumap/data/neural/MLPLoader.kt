package com.example.tsumap.data.neural

import android.content.Context
import com.example.tsumap.R
import com.google.gson.JsonParser
import com.example.tsumap.domain.model.neural.Activation
import com.example.tsumap.domain.model.neural.LayerData
import com.example.tsumap.domain.model.neural.MLP

object MLPLoader {

    private val activations = listOf(Activation.RELU, Activation.RELU, Activation.LINEAR)

    fun load(context: Context): MLP {
        val json = context.resources
            .openRawResource(R.raw.mlp_weights)
            .bufferedReader()
            .use { it.readText() }

        val root = JsonParser.parseString(json).asJsonObject
        val layersJson = root.getAsJsonArray("layers")

        val layers = layersJson.mapIndexed { index, element ->
            val layerObj = element.asJsonObject

            val wShape = layerObj.getAsJsonArray("W_shape")
            val inSize = wShape[0].asInt
            val outSize = wShape[1].asInt

            val flatW = layerObj.getAsJsonArray("W")
            val weights = Array(inSize) { i ->
                FloatArray(outSize) { j ->
                    flatW[i * outSize + j].asFloat
                }
            }

            val flatB = layerObj.getAsJsonArray("b")
            val biases = FloatArray(outSize) { j -> flatB[j].asFloat }

            LayerData(
                weights = weights,
                biases = biases,
                activation = activations[index]
            )
        }

        return MLP(layers)
    }
}