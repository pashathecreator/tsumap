package com.example.tsumap.ui.screens.ant

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.example.tsumap.ui.navigation.Screen

@Composable
fun LandmarkSelectionScreen(
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

    val landmarks by viewModel.landmarks.collectAsState()
    val selectedLandmarks by viewModel.selectedLandmarks.collectAsState()

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
                    text = "Куда идём?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Выбери достопримечательности",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            items(landmarks) { landmark ->
                Card(
                    onClick = { viewModel.toggleLandmark(landmark.name) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (landmark.name in selectedLandmarks)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surface
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Checkbox(
                            checked = landmark.name in selectedLandmarks,
                            onCheckedChange = { viewModel.toggleLandmark(landmark.name) }
                        )
                        Text(
                            text = "${landmark.emoji} ${landmark.name}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (landmark.name in selectedLandmarks)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }

        Surface(
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                if (selectedLandmarks.size < 2) {
                    Text(
                        text = "Выбери минимум 2 точки",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
                Button(
                    onClick = { navController.navigate(Screen.Ant.route) },
                    enabled = selectedLandmarks.size >= 2,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Построить маршрут")
                }
            }
        }
    }
}
