package com.example.tsumap.data.tree

import android.content.Context
import com.example.tsumap.R
import com.google.gson.JsonObject
import com.google.gson.JsonParser

object DecisionTreeLoader {

    fun load(context: Context): JsonObject {
        val json = context.resources.openRawResource(R.raw.decision_tree)
            .bufferedReader()
            .readText()
        return JsonParser.parseString(json).asJsonObject
    }
}
