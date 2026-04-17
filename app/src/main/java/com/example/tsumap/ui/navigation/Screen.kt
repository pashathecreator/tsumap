package com.example.tsumap.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object AStar : Screen("astar")
    object Cluster : Screen("cluster")
    object FoodSelection : Screen("food_selection")
    object Genetic : Screen("genetic")
    object LandmarkSelection : Screen("landmark_selection")
    object Ant : Screen("ant")
    object DecisionTree : Screen("decision_tree")
    object CafeSelection : Screen("cafe_selection")
    object Neural : Screen("neural")
}
