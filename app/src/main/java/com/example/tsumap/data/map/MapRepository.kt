package com.example.tsumap.data.map

import android.content.Context

class MapRepository(private val context: Context) {
    fun getGrid(): Array<IntArray> {
        return GridLoader.load(context)
    }
}