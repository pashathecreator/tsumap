package com.example.tsumap.data.repository

import android.content.Context
import android.graphics.Bitmap
import com.example.tsumap.data.map.GridLoader
import com.example.tsumap.data.map.MapImageLoader

class MapRepository(private val context: Context) {
    fun getGrid(): Array<IntArray> = GridLoader.load(context)
    fun getMapImage(): Bitmap = MapImageLoader.load(context)
}