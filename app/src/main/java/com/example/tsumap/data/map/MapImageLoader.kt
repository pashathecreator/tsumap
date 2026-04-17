package com.example.tsumap.data.map

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.tsumap.R

object MapImageLoader {
    fun load(context: Context): Bitmap =
        BitmapFactory.decodeResource(context.resources, R.drawable.map)
}