package com.example.tsumap.data.map

import android.content.Context
import com.example.tsumap.R

object GridLoader {

    fun load(context: Context): Array<IntArray> {
        val inputStream = context.resources.openRawResource(R.raw.campus_map)

        val lines = inputStream.bufferedReader().readLines()

        val height = lines.size
        val width = lines[0].length

        val grid = Array(height) { IntArray(width) }

        for (y in 0 until height) {
            val line = lines[y]
            for (x in 0 until width) {
                grid[y][x] = line[x].digitToInt()
            }
        }

        return grid
    }
}