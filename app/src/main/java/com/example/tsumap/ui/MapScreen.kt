package com.example.tsumap.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.unit.IntSize
import com.example.tsumap.R

@Composable
fun MapScreen() {
    val mapImage = ImageBitmap.imageResource(id = R.drawable.map)

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A))
    ) {
        val screenW = constraints.maxWidth.toFloat()
        val screenH = constraints.maxHeight.toFloat()

        val mapBaseW = screenW
        val mapBaseH = mapBaseW * mapImage.height / mapImage.width

        val initialScale = (screenH / mapBaseH).coerceAtLeast(1f)
        val minScale = initialScale

        var scale by remember { mutableFloatStateOf(initialScale) }
        var offset by remember { mutableStateOf(Offset.Zero) }

        fun clampOffset(s: Float, o: Offset): Offset {
            val scaledW = mapBaseW * s
            val scaledH = mapBaseH * s
            val minX = screenW - scaledW
            val maxX = 0f
            val minY = screenH - scaledH
            val maxY = 0f
            return Offset(
                x = o.x.coerceIn(minX.coerceAtMost(maxX), maxX),
                y = o.y.coerceIn(minY.coerceAtMost(maxY), maxY)
            )
        }

        LaunchedEffect(initialScale) {
            offset = clampOffset(initialScale, Offset.Zero)
        }

        val transformableState = rememberTransformableState { zoomChange, panChange, _ ->
            val newScale = (scale * zoomChange).coerceIn(minScale, 8f)
            val newOffset = offset + panChange
            scale = newScale
            offset = clampOffset(newScale, newOffset)
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = transformableState)
        ) {
            withTransform({
                translate(left = offset.x, top = offset.y)
                scale(scaleX = scale, scaleY = scale, pivot = Offset.Zero)
            }) {
                drawImage(
                    image = mapImage,
                    dstSize = IntSize(mapBaseW.toInt(), mapBaseH.toInt())
                )
            }
        }
    }
}