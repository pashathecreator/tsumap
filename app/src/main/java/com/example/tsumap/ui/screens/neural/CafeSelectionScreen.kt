package com.example.tsumap.ui.screens.neural

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavController
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.data.repository.RatingRepository
import com.example.tsumap.data.repository.TsuDatabase
import com.example.tsumap.ui.navigation.Screen

@Composable
fun CafeSelectionScreen(navController: NavController, parentEntry: NavBackStackEntry) {
    val context = LocalContext.current
    val db = TsuDatabase.getInstance(context)
    val viewModel: CafeSharedViewModel = viewModel(viewModelStoreOwner = parentEntry) {
        CafeSharedViewModel(
            ratingRepository = RatingRepository(db.ratingDao()),
            placeRepository = PlaceRepository(db.placeDao())
        )
    }

    val cafeList by viewModel.cafeList.collectAsState()
    val averageRatings by viewModel.averageRatings.collectAsState()

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
                    text = "Оценить заведение",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(cafeList) { cafe ->
                val avg = averageRatings[cafe.key]
                Card(
                    onClick = {
                        viewModel.selectCafe(cafe)
                        navController.navigate(Screen.Neural.route)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            Text(text = cafe.emoji, style = MaterialTheme.typography.headlineSmall)
                            Text(
                                text = cafe.name,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                        Text(
                            text = if (avg != null) "★ ${"%.1f".format(avg)}" else "—",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        }
    }
}