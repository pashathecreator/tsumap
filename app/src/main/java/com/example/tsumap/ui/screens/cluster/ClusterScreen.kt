package com.example.tsumap.ui.screens.cluster

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import com.example.tsumap.data.repository.RatingRepository
import com.example.tsumap.data.repository.TsuDatabase
import com.example.tsumap.ui.components.MapCanvas
import com.example.tsumap.ui.components.PlaceBottomSheet
import com.example.tsumap.ui.navigation.Screen

@Composable
fun ClusterScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel {
        val db = TsuDatabase.getInstance(context)
        ClusterViewModel(
            repository = MapRepository(context),
            placeRepository = PlaceRepository(db.placeDao()),
            ratingRepository = RatingRepository(db.ratingDao())
        )
    }

    val grid by viewModel.grid.collectAsState()
    val mapImage by viewModel.mapImage.collectAsState()
    val overlayItems by viewModel.overlayItems.collectAsState()
    val markers by viewModel.markers.collectAsState()
    val kValue by viewModel.kValue.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val legend by viewModel.legend.collectAsState()
    val selectedPlace by viewModel.selectedPlace.collectAsState()
    val selectedPlaceRating by viewModel.selectedPlaceRating.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
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
                        text = "Зоны еды",
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = "Кластеров: $kValue",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(120.dp)
                        )
                        Slider(
                            value = kValue.toFloat(),
                            onValueChange = { viewModel.setK(it.toInt()) },
                            valueRange = 2f..5f,
                            steps = 2,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    if (legend.isNotEmpty()) {
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(legend) { item ->
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(16.dp)
                                            .background(
                                                color = item.color,
                                                shape = MaterialTheme.shapes.small
                                            )
                                    )
                                    Text(
                                        text = item.name,
                                        style = MaterialTheme.typography.bodySmall
                                    )
                                }
                            }
                        }
                    }

                    Button(
                        onClick = { viewModel.runClustering() },
                        enabled = grid.isNotEmpty() && !isLoading,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        } else {
                            Text("Запустить кластеризацию")
                        }
                    }
                }
            }
        }

        selectedPlace?.let { place ->
            PlaceBottomSheet(
                place = place,
                averageRating = selectedPlaceRating,
                onDismiss = { viewModel.clearSelectedPlace() },
                onRate = if (place.type == "CAFE") {
                    { navController.navigate(Screen.CafeSelection.route) }
                } else null
            )
        }
    }
}