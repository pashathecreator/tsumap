package com.example.tsumap.ui.screens.genetic

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.domain.usecase.genetic.GeneticTspRouteUseCase
import com.example.tsumap.ui.components.Cell
import com.example.tsumap.ui.components.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

enum class FoodCategory(val label: String, val emoji: String) {
    COFFEE("Кофе", "☕"),
    PANCAKES("Блины", "🥞"),
    MEAL("Обед", "🍱"),
    SNACK("Перекус", "🥪")
}

data class FoodPlace(val row: Int, val col: Int, val category: FoodCategory)

private val CATEGORY_COLORS = mapOf(
    FoodCategory.COFFEE to Color(0xFF795548),
    FoodCategory.PANCAKES to Color(0xFFFF9800),
    FoodCategory.MEAL to Color(0xFF4CAF50),
    FoodCategory.SNACK to Color(0xFF2196F3)
)

class FoodSharedViewModel(private val repository: MapRepository, private val placeRepository: PlaceRepository) : ViewModel() {

    private val _grid = MutableStateFlow<Array<IntArray>>(emptyArray())
    val grid: StateFlow<Array<IntArray>> = _grid

    private val _mapImage = MutableStateFlow<ImageBitmap?>(null)
    val mapImage: StateFlow<ImageBitmap?> = _mapImage

    private val _foodPlaces = MutableStateFlow<List<FoodPlace>>(emptyList())
    val foodPlaces: StateFlow<List<FoodPlace>> = _foodPlaces

    private val _selectedCategories = MutableStateFlow<Set<FoodCategory>>(emptySet())
    val selectedCategories: StateFlow<Set<FoodCategory>> = _selectedCategories

    private val _overlayItems = MutableStateFlow<List<Cell>>(emptyList())
    val overlayItems: StateFlow<List<Cell>> = _overlayItems

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _totalDistance = MutableStateFlow<Int?>(null)
    val totalDistance: StateFlow<Int?> = _totalDistance

    private val _routePlaces = MutableStateFlow<List<FoodPlace>>(emptyList())
    val routePlaces: StateFlow<List<FoodPlace>> = _routePlaces

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val grid = repository.getGrid()
            _grid.value = grid
            _mapImage.value = repository.getMapImage().asImageBitmap()
        }

        viewModelScope.launch {
            placeRepository.getCafes().collect { cafes ->
                if (cafes.isEmpty()) return@collect
                val places = cafes.map { place ->
                    val category = when (place.key) {
                        "starbooks", "coffe1", "coffe2" -> FoodCategory.COFFEE
                        "pancakes" -> FoodCategory.PANCAKES
                        "cafe_main" -> FoodCategory.MEAL
                        "yarche", "our" -> FoodCategory.SNACK
                        else -> FoodCategory.SNACK
                    }
                    FoodPlace(place.gridRow, place.gridCol, category)
                }
                _foodPlaces.value = places
                _markers.value = cafes.map { Marker(it.gridRow, it.gridCol, it.emoji) }
            }
        }
    }

    fun toggleCategory(category: FoodCategory) {
        val current = _selectedCategories.value.toMutableSet()
        if (category in current) current.remove(category) else current.add(category)
        _selectedCategories.value = current
        _overlayItems.value = emptyList()
        _totalDistance.value = null
        _routePlaces.value = emptyList()
    }

    fun findRoute() {
        val selected = _selectedCategories.value
        if (selected.isEmpty()) return

        val targetPlaces = selected.mapNotNull { category ->
            _foodPlaces.value.firstOrNull { it.category == category }
        }
        if (targetPlaces.size < 2) return

        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            val points = targetPlaces.map { Pair(it.row, it.col) }
            val result = GeneticTspRouteUseCase(_grid.value).invoke(points)
            _totalDistance.value = result.totalDistance

            val orderedPlaces = result.order.mapNotNull { pos ->
                targetPlaces.firstOrNull { it.row == pos.first && it.col == pos.second }
            }
            _routePlaces.value = orderedPlaces

            val cells = mutableListOf<Cell>()
            val delayMs = (1500L / result.fullPath.size.coerceAtLeast(1)).coerceIn(5L, 50L)

            for ((r, c) in result.fullPath) {
                cells.add(Cell(r, c, Color.Blue.copy(alpha = 0.5f)))
                _overlayItems.value = cells.toList()
                delay(delayMs)
            }

            orderedPlaces.forEachIndexed { i, place ->
                val color = when (i) {
                    0 -> Color.Green.copy(alpha = 0.8f)
                    orderedPlaces.lastIndex -> Color.Red.copy(alpha = 0.8f)
                    else -> CATEGORY_COLORS[place.category]!!.copy(alpha = 0.8f)
                }
                cells.add(Cell(place.row, place.col, color))
            }
            _overlayItems.value = cells.toList()
            _isLoading.value = false
        }
    }
}
