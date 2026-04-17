package com.example.tsumap.ui.screens.genetic

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
import com.example.tsumap.ui.navigation.Screen

@Composable
fun FoodSelectionScreen(
    navController: NavController,
    parentEntry: NavBackStackEntry
) {
    val context = LocalContext.current
    val viewModel = viewModel<FoodSharedViewModel>(parentEntry) {
        val db = TsuDatabase.getInstance(context)
        FoodSharedViewModel(
            repository = MapRepository(context),
            placeRepository = PlaceRepository(db.placeDao())
        )
    }

    val foodPlaces by viewModel.foodPlaces.collectAsState()
    val selectedCategories by viewModel.selectedCategories.collectAsState()

    val availableCategories = remember(foodPlaces) {
        foodPlaces.map { it.category }.toSet()
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
                    text = "Что купить?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Выбери что хочешь купить",
                style = MaterialTheme.typography.bodyLarge
            )

            availableCategories.forEach { category ->
                Card(
                    onClick = { viewModel.toggleCategory(category) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (category in selectedCategories)
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
                            checked = category in selectedCategories,
                            onCheckedChange = { viewModel.toggleCategory(category) }
                        )
                        Text(
                            text = "${category.emoji} ${category.label}",
                            style = MaterialTheme.typography.titleMedium,
                            color = if (category in selectedCategories)
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
            Button(
                onClick = { navController.navigate(Screen.Genetic.route) },
                enabled = selectedCategories.isNotEmpty(),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text("Построить маршрут")
            }
        }
    }
}
