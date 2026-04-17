package com.example.tsumap.ui.screens.neural


import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.tsumap.data.repository.RatingRepository
import com.example.tsumap.data.repository.TsuDatabase
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import com.example.tsumap.data.repository.PlaceRepository

@Composable
fun NeuralScreen(navController: NavController, parentEntry: NavBackStackEntry) {
    val context = LocalContext.current

    val db = TsuDatabase.getInstance(context)

    val cafeViewModel: CafeSharedViewModel = viewModel(viewModelStoreOwner = parentEntry) {
        CafeSharedViewModel(
            ratingRepository = RatingRepository(db.ratingDao()),
            placeRepository = PlaceRepository(db.placeDao())
        )
    }
    val neuralViewModel: NeuralViewModel = viewModel {
        NeuralViewModel(context, RatingRepository(db.ratingDao()))
    }

    val selectedCafe by cafeViewModel.selectedCafe.collectAsState()
    val pixels by neuralViewModel.pixels.collectAsState()
    val predictedDigit by neuralViewModel.predictedDigit.collectAsState()
    val isSaved by neuralViewModel.isSaved.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        Surface(tonalElevation = 4.dp, modifier = Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("←")
                }
                Text(
                    text = "Оцените: ${selectedCafe?.name ?: ""}",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                text = "Нарисуйте цифру от 0 до 9",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .aspectRatio(1f)
                    .background(Color.Black, RoundedCornerShape(16.dp))
            ) {
                Canvas(
                    modifier = Modifier
                        .fillMaxSize()
                        .pointerInput(Unit) {
                            detectDragGestures { change, _ ->
                                val cellW = size.width / 28f
                                val cellH = size.height / 28f
                                val col = (change.position.x / cellW).toInt()
                                val row = (change.position.y / cellH).toInt()
                                neuralViewModel.drawPixel(row, col)
                            }
                        }
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val cellW = size.width / 28f
                                val cellH = size.height / 28f
                                val col = (offset.x / cellW).toInt()
                                val row = (offset.y / cellH).toInt()
                                neuralViewModel.drawPixel(row, col)
                            }
                        }
                ) {
                    val cellW = size.width / 28f
                    val cellH = size.height / 28f
                    pixels.forEachIndexed { row, rowData ->
                        rowData.forEachIndexed { col, value ->
                            if (value > 0f) {
                                drawRect(
                                    color = Color.White,
                                    topLeft = Offset(col * cellW, row * cellH),
                                    size = Size(cellW, cellH)
                                )
                            }
                        }
                    }
                }
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Button(
                    onClick = { neuralViewModel.predict() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Распознать")
                }
                OutlinedButton(
                    onClick = { neuralViewModel.clear() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Очистить")
                }
            }

            if (predictedDigit != null) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ваша оценка:",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = predictedDigit.toString(),
                        fontSize = 72.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    if (isSaved) {
                        Text(
                            text = "✓ Оценка сохранена",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    } else {
                        Button(
                            onClick = { selectedCafe?.let { neuralViewModel.saveRating(it.key) } },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Подтвердить оценку")
                        }
                    }
                }
            }
        }
    }
}
