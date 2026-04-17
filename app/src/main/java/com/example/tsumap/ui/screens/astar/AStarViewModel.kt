package com.example.tsumap.ui.screens.astar

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.domain.usecase.astar.AStarUseCase
import com.example.tsumap.ui.components.Cell
import com.example.tsumap.ui.components.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class AStarViewModel(
    private val repository: MapRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _grid = MutableStateFlow<Array<IntArray>>(emptyArray())
    val grid: StateFlow<Array<IntArray>> = _grid

    private val _mapImage = MutableStateFlow<ImageBitmap?>(null)
    val mapImage: StateFlow<ImageBitmap?> = _mapImage

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    private val _startPoint = MutableStateFlow<Pair<Int, Int>?>(null)
    val startPoint: StateFlow<Pair<Int, Int>?> = _startPoint

    private val _endPoint = MutableStateFlow<Pair<Int, Int>?>(null)
    val endPoint: StateFlow<Pair<Int, Int>?> = _endPoint

    private val _overlayItems = MutableStateFlow<List<Cell>>(emptyList())
    val overlayItems: StateFlow<List<Cell>> = _overlayItems

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _pathFound = MutableStateFlow<Boolean?>(null)
    val pathFound: StateFlow<Boolean?> = _pathFound

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val grid = repository.getGrid()
            _grid.value = grid
            _mapImage.value = repository.getMapImage().asImageBitmap()

            val places = placeRepository.getAll().first()
            _markers.value = places.map { Marker(it.gridRow, it.gridCol, it.emoji) }
        }
    }

    fun onMapTap(row: Int, col: Int) {
        if (_grid.value.isEmpty()) return
        val snapped = snapToWalkable(row, col)
        if (_startPoint.value == null) {
            _startPoint.value = snapped
        } else if (_endPoint.value == null) {
            _endPoint.value = snapped
        } else {
            _startPoint.value = snapped
            _endPoint.value = null
            _pathFound.value = null
        }
        rebuildOverlay(emptyList())
    }

    private fun snapToWalkable(row: Int, col: Int): Pair<Int, Int> {
        val grid = _grid.value
        if (grid[row][col] > 0) return Pair(row, col)
        for (radius in 1..20) {
            for (dr in -radius..radius) {
                for (dc in -radius..radius) {
                    if (kotlin.math.abs(dr) != radius && kotlin.math.abs(dc) != radius) continue
                    val nr = row + dr
                    val nc = col + dc
                    if (nr in grid.indices && nc in grid[0].indices && grid[nr][nc] > 0) {
                        return Pair(nr, nc)
                    }
                }
            }
        }
        return Pair(row, col)
    }
    fun findPath() {
        val start = _startPoint.value ?: return
        val end = _endPoint.value ?: return
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            val path = AStarUseCase(_grid.value).invoke(start, end)
            _pathFound.value = path.isNotEmpty()
            _isLoading.value = false

            val cells = mutableListOf<Cell>()
            _startPoint.value?.let { (r, c) ->
                cells.add(Cell(r, c, Color.Green.copy(alpha = 0.8f)))
            }
            _overlayItems.value = cells.toList()

            val delayMs = (1500L / path.size.coerceAtLeast(1)).coerceIn(5L, 50L)
            for ((r, c) in path) {
                cells.add(Cell(r, c, Color.Blue.copy(alpha = 0.5f)))
                _overlayItems.value = cells.toList()
                delay(delayMs)
            }

            _endPoint.value?.let { (r, c) ->
                cells.add(Cell(r, c, Color.Red.copy(alpha = 0.8f)))
            }
            _overlayItems.value = cells.toList()
        }
    }

    fun reset() {
        _startPoint.value = null
        _endPoint.value = null
        _pathFound.value = null
        _overlayItems.value = emptyList()
    }

    private fun rebuildOverlay(path: List<Pair<Int, Int>>) {
        val cells = mutableListOf<Cell>()
        path.forEach { (r, c) -> cells.add(Cell(r, c, Color.Blue.copy(alpha = 0.5f))) }
        _startPoint.value?.let { (r, c) -> cells.add(Cell(r, c, Color.Green.copy(alpha = 0.8f))) }
        _endPoint.value?.let { (r, c) -> cells.add(Cell(r, c, Color.Red.copy(alpha = 0.8f))) }
        _overlayItems.value = cells
    }
}