package com.example.tsumap.ui.screens.astar

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.data.repository.TsuDatabase
import com.example.tsumap.ui.components.MapCanvas

@Composable
fun AStarScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel {
        val db = TsuDatabase.getInstance(context)
        AStarViewModel(
            repository = MapRepository(context),
            placeRepository = PlaceRepository(db.placeDao())
        )
    }

    val grid by viewModel.grid.collectAsState()
    val mapImage by viewModel.mapImage.collectAsState()
    val overlayItems by viewModel.overlayItems.collectAsState()
    val startPoint by viewModel.startPoint.collectAsState()
    val endPoint by viewModel.endPoint.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val pathFound by viewModel.pathFound.collectAsState()
    val markers by viewModel.markers.collectAsState()

    val statusText = when {
        grid.isEmpty() -> "Загрузка..."
        isLoading -> "Поиск маршрута..."
        pathFound == true -> "Путь найден: ${overlayItems.size} шагов"
        pathFound == false -> "Маршрут не найден"
        startPoint == null -> "Выберите начальную точку"
        endPoint == null -> "Выберите конечную точку"
        else -> "Готово. Нажмите «Найти маршрут»"
    }

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            color = MaterialTheme.colorScheme.primary,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { navController.popBackStack() }) {
                    Text("←", color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(
                    text = "Маршрут A*",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        mapImage?.let { image ->
            MapCanvas(
                mapImage = image,
                grid = grid,
                overlayItems = overlayItems,
                markers = markers,
                onTap = { row, col -> viewModel.onMapTap(row, col) },
                modifier = Modifier.weight(1f)
            )
        }

        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = statusText,
                    style = MaterialTheme.typography.bodyMedium
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = { viewModel.findPath() },
                        enabled = startPoint != null && endPoint != null && !isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Найти маршрут")
                        }
                    }
                    OutlinedButton(
                        onClick = { viewModel.reset() },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Сбросить")
                    }
                }
            }
        }
    }
}
