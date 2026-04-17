package com.example.tsumap.ui.screens.ant

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.data.repository.TsuDatabase
import com.example.tsumap.ui.components.MapCanvas

@Composable
fun AntScreen(
    navController: NavController,
    parentEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val viewModel = viewModel<AntSharedViewModel>(parentEntry) {
        val db = TsuDatabase.getInstance(context)
        AntSharedViewModel(
            repository = MapRepository(context),
            placeRepository = PlaceRepository(db.placeDao())
        )
    }

    val grid by viewModel.grid.collectAsState()
    val mapImage by viewModel.mapImage.collectAsState()
    val overlayItems by viewModel.overlayItems.collectAsState()
    val markers by viewModel.markers.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val totalDistance by viewModel.totalDistance.collectAsState()
    val routeLandmarks by viewModel.routeLandmarks.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.findTour()
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
                    text = "Экскурсия по роще",
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
                modifier = Modifier.weight(1f)
            )
        }

        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isLoading) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Text(
                            text = "Строим маршрут...",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }

                if (totalDistance != null) {
                    Text(
                        text = "Расстояние: $totalDistance шагов",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                if (routeLandmarks.isNotEmpty()) {
                    Text(
                        text = routeLandmarks.joinToString(" → ") { "${it.emoji} ${it.name}" },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}
