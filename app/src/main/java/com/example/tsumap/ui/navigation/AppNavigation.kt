package com.example.tsumap.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.example.tsumap.ui.screens.ant.AntScreen
import com.example.tsumap.ui.screens.ant.LandmarkSelectionScreen
import com.example.tsumap.ui.screens.astar.AStarScreen
import com.example.tsumap.ui.screens.cluster.ClusterScreen
import com.example.tsumap.ui.screens.genetic.FoodSelectionScreen
import com.example.tsumap.ui.screens.genetic.GeneticScreen
import com.example.tsumap.ui.screens.home.HomeScreen
import com.example.tsumap.ui.screens.neural.CafeSelectionScreen
import com.example.tsumap.ui.screens.neural.NeuralScreen
import com.example.tsumap.ui.screens.tree.DecisionTreeScreen

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Home.route) {

        composable(Screen.Home.route) {
            HomeScreen(navController = navController)
        }

        composable(Screen.AStar.route) {
            AStarScreen(navController = navController)
        }

        composable(Screen.Cluster.route) {
            ClusterScreen(navController = navController)
        }

        navigation(
            startDestination = Screen.LandmarkSelection.route,
            route = "ant_flow"
        ) {
            composable(Screen.LandmarkSelection.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("ant_flow") }
                LandmarkSelectionScreen(navController = navController, parentEntry = parentEntry)
            }
            composable(Screen.Ant.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("ant_flow") }
                AntScreen(navController = navController, parentEntry = parentEntry)
            }
        }

        composable(Screen.DecisionTree.route) {
            DecisionTreeScreen(navController = navController)
        }

        navigation(
            startDestination = Screen.FoodSelection.route,
            route = "food_flow"
        ) {
            composable(Screen.FoodSelection.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("food_flow") }
                FoodSelectionScreen(navController = navController, parentEntry = parentEntry)
            }
            composable(Screen.Genetic.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("food_flow") }
                GeneticScreen(navController = navController, parentEntry = parentEntry)
            }
        }

        navigation(
            startDestination = Screen.CafeSelection.route,
            route = "cafe_flow"
        ) {
            composable(Screen.CafeSelection.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("cafe_flow") }
                CafeSelectionScreen(navController = navController, parentEntry = parentEntry)
            }
            composable(Screen.Neural.route) { entry ->
                val parentEntry = remember(entry) { navController.getBackStackEntry("cafe_flow") }
                NeuralScreen(navController = navController, parentEntry = parentEntry)
            }
        }
    }
}
