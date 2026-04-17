package com.example.tsumap.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntSize

data class Cell(val row: Int, val col: Int, val color: Color)
data class Marker(val row: Int, val col: Int, val emoji: String)

@Composable
fun MapCanvas(
    modifier: Modifier = Modifier,
    mapImage: ImageBitmap,
    grid: Array<IntArray>,
    overlayItems: List<Cell> = emptyList(),
    markers: List<Marker> = emptyList(),
    onTap: ((row: Int, col: Int) -> Unit)? = null,
) {
    BoxWithConstraints(modifier = modifier.fillMaxSize()) {
        val screenW = constraints.maxWidth.toFloat()
        val screenH = constraints.maxHeight.toFloat()
        val mapW = screenW
        val mapH = mapW * mapImage.height / mapImage.width
        val scale = screenH / mapH

        var offset by remember {
            mutableStateOf(
                Offset(
                    x = (screenW - mapW * scale) / 2f,
                    y = (screenH - mapH * scale) / 2f
                )
            )
        }

        fun clampOffset(o: Offset) = Offset(
            x = o.x.coerceIn(screenW - mapW * scale, 0f),
            y = o.y.coerceIn(screenH - mapH * scale, 0f)
        )

        val rows = grid.size
        val cols = if (rows > 0) grid[0].size else 1
        val cellW = mapW / cols
        val cellH = mapH / rows

        fun screenToGrid(screenX: Float, screenY: Float): Pair<Int, Int> {
            val localX = (screenX - offset.x) / scale
            val localY = (screenY - offset.y) / scale
            val col = (localX / cellW).toInt().coerceIn(0, cols - 1)
            val row = (localY / cellH).toInt().coerceIn(0, rows - 1)
            return Pair(row, col)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures { _, dragAmount ->
                        offset = clampOffset(offset + Offset(dragAmount.x, dragAmount.y))
                    }
                }
                .then(
                    if (onTap != null) {
                        Modifier.pointerInput(Unit) {
                            detectTapGestures { tapOffset ->
                                val (row, col) = screenToGrid(tapOffset.x, tapOffset.y)
                                onTap(row, col)
                            }
                        }
                    } else Modifier
                )
        ) {
            withTransform({
                translate(left = offset.x, top = offset.y)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
            }) {
                drawImage(image = mapImage, dstSize = IntSize(mapW.toInt(), mapH.toInt()))
                drawOverlays(overlayItems, cellW, cellH)
                drawMarkers(markers, cellW, cellH)
            }
        }
    }
}

private fun DrawScope.drawOverlays(items: List<Cell>, cellW: Float, cellH: Float) {
    for (item in items) {
        drawRect(
            color = item.color,
            topLeft = Offset(item.col * cellW, item.row * cellH),
            size = Size(cellW, cellH)
        )
    }
}

private fun DrawScope.drawMarkers(markers: List<Marker>, cellW: Float, cellH: Float) {
    val paint = android.graphics.Paint().apply {
        textSize = cellW * 2.5f
        textAlign = android.graphics.Paint.Align.CENTER
        isAntiAlias = true
    }
    for (marker in markers) {
        val x = marker.col * cellW + cellW / 2f
        val y = marker.row * cellH + cellH / 2f + paint.textSize / 3f
        drawContext.canvas.nativeCanvas.drawText(marker.emoji, x, y, paint)
    }
}
