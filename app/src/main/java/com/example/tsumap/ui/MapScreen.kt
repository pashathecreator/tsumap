package com.example.tsumap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import com.example.tsumap.R
import androidx.compose.ui.graphics.Paint
import android.graphics.BlurMaskFilter
private fun drawGrid(
    scope: DrawScope,
    mapW: Float,
    mapH: Float,
    grid: Array<IntArray>,
    gridColor: Color = Color(0x334A90E2),
    strokeWidth: Float = 1f
) {
    val rows = grid.size
    val cols = if (rows > 0) grid[0].size else return

    val cellW = mapW / cols
    val cellH = mapH / rows

    val walkablePaint = Paint().apply {
        color = Color.Red.copy(alpha = 0.9f)
        asFrameworkPaint().maskFilter = BlurMaskFilter(cellW * 0.5f, BlurMaskFilter.Blur.NORMAL)
    }

    val entrancePaint = Paint().apply {
        color = Color.Blue
        asFrameworkPaint().maskFilter = BlurMaskFilter(cellW * 0.5f, BlurMaskFilter.Blur.NORMAL)
    }

    if (grid.isNotEmpty()) {
        for (row in 0 until rows) {
            for (col in 0 until cols) {
                val cell = grid[row][col]
                if (cell == 1) continue

                val paint = if (cell == 2) entrancePaint else walkablePaint
                val x = col * cellW
                val y = row * cellH

                scope.drawContext.canvas.drawRect(
                    left = x,
                    top = y,
                    right = x + cellW,
                    bottom = y + cellH,
                    paint = paint
                )
            }
        }
    }

    for (col in 0..cols) {
        val x = col * cellW
        scope.drawLine(
            color = gridColor,
            start = Offset(x, 0f),
            end = Offset(x, mapH),
            strokeWidth = strokeWidth
        )
    }
    for (row in 0..rows) {
        val y = row * cellH
        scope.drawLine(
            color = gridColor,
            start = Offset(0f, y),
            end = Offset(mapW, y),
            strokeWidth = strokeWidth
        )
    }
}

@Composable
fun MapScreen(viewModel: MapViewModel) {
    val grid by produceState<Array<IntArray>>(initialValue = emptyArray()) {
        value = viewModel.grid.await()
    }

    val mapImage = ImageBitmap.imageResource(id = R.drawable.map)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        val screenW = constraints.maxWidth.toFloat()
        val screenH = constraints.maxHeight.toFloat()

        val mapW = screenW
        val mapH = mapW * mapImage.height / mapImage.width
        val initialScale = screenH / mapH

        var offset by remember {
            mutableStateOf(
                Offset(
                    x = (screenW - mapW * initialScale) / 2f,
                    y = (screenH - mapH * initialScale) / 2f
                )
            )
        }

        fun clampOffset(o: Offset): Offset {
            val scaledW = mapW * initialScale
            val scaledH = mapH * initialScale
            return Offset(
                x = o.x.coerceIn(screenW - scaledW, 0f),
                y = o.y.coerceIn(screenH - scaledH, 0f)
            )
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offset = clampOffset(offset + Offset(dragAmount.x, dragAmount.y))
                    }
                }
        ) {
            withTransform({
                translate(left = offset.x, top = offset.y)
                scale(scaleX = initialScale, scaleY = initialScale, pivot = Offset.Zero)
            }) {
                drawImage(
                    image = mapImage,
                    dstSize = IntSize(mapW.toInt(), mapH.toInt())
                )

                drawGrid(
                    scope = this,
                    mapW = mapW,
                    mapH = mapH,
                    grid=grid
                )
            }
        }
    }
}