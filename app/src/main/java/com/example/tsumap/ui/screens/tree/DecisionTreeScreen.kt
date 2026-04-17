package com.example.tsumap.ui.screens.tree

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.tsumap.ui.navigation.Screen

@Composable
fun DecisionTreeScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = viewModel { DecisionTreeViewModel(context) }

    val currentStep by viewModel.currentStep.collectAsState()
    val result by viewModel.result.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {

        Surface(
            color = MaterialTheme.colorScheme.primary, modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 4.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (currentStep > 0 && result == null) viewModel.back()
                    else navController.popBackStack()
                }) {
                    Text("←", color = MaterialTheme.colorScheme.onPrimary)
                }
                Text(
                    text = "Куда на обед?",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }

        if (result == null) {
            val step = viewModel.steps[currentStep]

            LinearProgressIndicator(
                progress = { (currentStep + 1).toFloat() / viewModel.steps.size },
                modifier = Modifier.fillMaxWidth()
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Шаг ${currentStep + 1} из ${viewModel.steps.size}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
                Text(
                    text = step.label, style = MaterialTheme.typography.titleLarge
                )
                Spacer(modifier = Modifier.height(8.dp))
                step.options.forEach { (value, label) ->
                    Card(
                        onClick = { viewModel.selectOption(value) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surface
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            contentAlignment = Alignment.CenterStart
                        ) {
                            Text(
                                text = label, style = MaterialTheme.typography.bodyLarge
                            )
                        }
                    }
                }
            }
        } else {
            if (isLoading) {
                Box(
                    modifier = Modifier.weight(1f), contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else {
                val placeName = viewModel.placeNames[result!!.place] ?: result!!.place

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Рекомендуем",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                    Text(
                        text = placeName, style = MaterialTheme.typography.displaySmall
                    )

                    HorizontalDivider()

                    Text(
                        text = "Путь решения:", style = MaterialTheme.typography.bodyMedium
                    )
                    result!!.path.forEach { step ->
                        Text(
                            text = step,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                }

                Surface(
                    tonalElevation = 8.dp, modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = {
                                navController.navigate(Screen.CafeSelection.route)
                            }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Оценить заведение")
                        }
                        OutlinedButton(
                            onClick = { viewModel.reset() }, modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Начать заново")
                        }
                    }
                }
            }
        }
    }
}
