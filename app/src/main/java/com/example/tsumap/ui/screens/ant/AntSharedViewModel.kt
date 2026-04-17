package com.example.tsumap.ui.screens.ant

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.domain.usecase.ant.AntColonyTspRouteUseCase
import com.example.tsumap.ui.components.Cell
import com.example.tsumap.ui.components.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class Landmark(val name: String, val emoji: String, val row: Int, val col: Int)

class AntSharedViewModel(
    private val repository: MapRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _grid = MutableStateFlow<Array<IntArray>>(emptyArray())
    val grid: StateFlow<Array<IntArray>> = _grid

    private val _mapImage = MutableStateFlow<ImageBitmap?>(null)
    val mapImage: StateFlow<ImageBitmap?> = _mapImage

    private val _landmarks = MutableStateFlow<List<Landmark>>(emptyList())
    val landmarks: StateFlow<List<Landmark>> = _landmarks

    private val _selectedLandmarks = MutableStateFlow<Set<String>>(emptySet())
    val selectedLandmarks: StateFlow<Set<String>> = _selectedLandmarks

    private val _overlayItems = MutableStateFlow<List<Cell>>(emptyList())
    val overlayItems: StateFlow<List<Cell>> = _overlayItems

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _totalDistance = MutableStateFlow<Int?>(null)
    val totalDistance: StateFlow<Int?> = _totalDistance

    private val _routeLandmarks = MutableStateFlow<List<Landmark>>(emptyList())
    val routeLandmarks: StateFlow<List<Landmark>> = _routeLandmarks

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val grid = repository.getGrid()
            _grid.value = grid
            _mapImage.value = repository.getMapImage().asImageBitmap()

            val places = placeRepository.getAll().first()
                .filter { it.type == "BUILDING" || it.type == "MONUMENT" }

            val landmarks = places.map { Landmark(it.name, it.emoji, it.gridRow, it.gridCol) }
            _landmarks.value = landmarks
            _markers.value = landmarks.map { Marker(it.row, it.col, it.emoji) }
        }
    }

    fun toggleLandmark(name: String) {
        val current = _selectedLandmarks.value.toMutableSet()
        if (name in current) current.remove(name) else current.add(name)
        _selectedLandmarks.value = current
        _overlayItems.value = emptyList()
        _totalDistance.value = null
        _routeLandmarks.value = emptyList()
    }

    fun findTour() {
        val selected = _selectedLandmarks.value
        if (selected.size < 2) return
        val targetLandmarks = _landmarks.value.filter { it.name in selected }

        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            val points = targetLandmarks.map { Pair(it.row, it.col) }
            val result = AntColonyTspRouteUseCase(_grid.value).invoke(points)
            _totalDistance.value = result.totalDistance

            val orderedLandmarks = result.order.mapNotNull { pos ->
                targetLandmarks.firstOrNull { it.row == pos.first && it.col == pos.second }
            }
            _routeLandmarks.value = orderedLandmarks

            val cells = mutableListOf<Cell>()
            val delayMs = (1500L / result.fullPath.size.coerceAtLeast(1)).coerceIn(5L, 50L)

            for ((r, c) in result.fullPath) {
                cells.add(Cell(r, c, Color(0xFFFF6B00).copy(alpha = 0.5f)))
                _overlayItems.value = cells.toList()
                delay(delayMs)
            }

            orderedLandmarks.forEachIndexed { i, landmark ->
                val color = when (i) {
                    0 -> Color.Green.copy(alpha = 0.8f)
                    orderedLandmarks.lastIndex -> Color.Red.copy(alpha = 0.8f)
                    else -> Color(0xFFFF6B00).copy(alpha = 0.8f)
                }
                cells.add(Cell(landmark.row, landmark.col, color))
            }
            _overlayItems.value = cells.toList()
            _isLoading.value = false
        }
    }
}