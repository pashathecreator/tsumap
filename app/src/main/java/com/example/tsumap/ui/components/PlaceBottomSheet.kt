package com.example.tsumap.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tsumap.data.place.PlaceEntity

@Composable
fun PlaceBottomSheet(
    place: PlaceEntity,
    averageRating: Float?,
    onDismiss: () -> Unit,
    onRate: (() -> Unit)?
) {
    val context = LocalContext.current
    val resId = context.resources.getIdentifier(place.imageResName, "drawable", context.packageName)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f))
            .clickable { onDismiss() }
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .clickable(enabled = false) {},
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            tonalElevation = 8.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 32.dp)
            ) {
                if (resId != 0) {
                    androidx.compose.foundation.Image(
                        painter = painterResource(id = resId),
                        contentDescription = place.name,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                    )
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(text = place.emoji, fontSize = 28.sp)
                        Text(
                            text = place.name,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Text(
                        text = if (averageRating != null)
                            "⭐ ${"%.1f".format(averageRating)}"
                        else
                            "Оценок пока нет",
                        style = MaterialTheme.typography.bodyLarge,
                        color = if (averageRating != null)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    if (onRate != null) {
                        Button(
                            onClick = onRate,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Оценить заведение")
                        }
                    }
                }
            }
        }
    }
}