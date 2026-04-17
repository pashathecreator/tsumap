package com.example.tsumap.ui.screens.cluster

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.place.PlaceEntity
import com.example.tsumap.data.repository.MapRepository
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.data.repository.RatingRepository
import com.example.tsumap.domain.usecase.cluster.ClusterCafesUseCase
import com.example.tsumap.ui.components.Cell
import com.example.tsumap.ui.components.Marker
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

private val CLUSTER_COLORS = listOf(
    Color.Red.copy(alpha = 0.4f),
    Color.Blue.copy(alpha = 0.4f),
    Color.Green.copy(alpha = 0.4f),
    Color.Yellow.copy(alpha = 0.4f),
    Color.Magenta.copy(alpha = 0.4f)
)

data class ClusterLegendItem(val name: String, val color: Color)

class ClusterViewModel(
    private val repository: MapRepository,
    private val placeRepository: PlaceRepository,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val _grid = MutableStateFlow<Array<IntArray>>(emptyArray())
    val grid: StateFlow<Array<IntArray>> = _grid

    private val _mapImage = MutableStateFlow<ImageBitmap?>(null)
    val mapImage: StateFlow<ImageBitmap?> = _mapImage

    private val _cafePoints = MutableStateFlow<List<Pair<Int, Int>>>(emptyList())

    private val _kValue = MutableStateFlow(3)
    val kValue: StateFlow<Int> = _kValue

    private val _overlayItems = MutableStateFlow<List<Cell>>(emptyList())
    val overlayItems: StateFlow<List<Cell>> = _overlayItems

    private val _markers = MutableStateFlow<List<Marker>>(emptyList())
    val markers: StateFlow<List<Marker>> = _markers

    private val _legend = MutableStateFlow<List<ClusterLegendItem>>(emptyList())
    val legend: StateFlow<List<ClusterLegendItem>> = _legend

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _selectedPlace = MutableStateFlow<PlaceEntity?>(null)
    val selectedPlace: StateFlow<PlaceEntity?> = _selectedPlace

    private val _selectedPlaceRating = MutableStateFlow<Float?>(null)
    val selectedPlaceRating: StateFlow<Float?> = _selectedPlaceRating

    init {
        viewModelScope.launch(Dispatchers.IO) {
            val grid = repository.getGrid()
            _grid.value = grid
            _mapImage.value = repository.getMapImage().asImageBitmap()

            val places = placeRepository.getAll().first()
            _markers.value = places.map { Marker(it.gridRow, it.gridCol, it.emoji) }

            val cafes = placeRepository.getCafes().first()
            _cafePoints.value = cafes.map { Pair(it.gridRow, it.gridCol) }
        }
    }

    fun setK(k: Int) {
        _kValue.value = k
    }

    fun onMapTap(row: Int, col: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val place = placeRepository.getByCoords(row, col)
            if (place != null) {
                _selectedPlace.value = place
                _selectedPlaceRating.value = ratingRepository.getAverageRating(place.key).first()
            }
        }
    }

    fun clearSelectedPlace() {
        _selectedPlace.value = null
        _selectedPlaceRating.value = null
    }

    fun runClustering() {
        viewModelScope.launch(Dispatchers.Default) {
            _isLoading.value = true
            val grid = _grid.value
            val cafePoints = _cafePoints.value
            val result = ClusterCafesUseCase().invoke(cafePoints, _kValue.value, nInit = 30)

            val cells = mutableListOf<Cell>()
            val markers = mutableListOf<Marker>()
            val legend = mutableListOf<ClusterLegendItem>()
            val radius = 5

            result.clusters.forEachIndexed { index, cluster ->
                val color = CLUSTER_COLORS[index % CLUSTER_COLORS.size]
                cluster.members.forEach { (r, c) ->
                    for (dr in -radius..radius) {
                        for (dc in -radius..radius) {
                            val nr = r + dr
                            val nc = c + dc
                            if (nr in grid.indices && nc in grid[0].indices) {
                                cells.add(Cell(nr, nc, color))
                            }
                        }
                    }
                }
                markers.add(Marker(cluster.medoid.first, cluster.medoid.second, "⭐"))
                legend.add(ClusterLegendItem("Кластер ${index + 1}", color))
            }

            cafePoints.forEach { (r, c) ->
                val emoji = placeRepository.getByCoords(r, c)?.emoji ?: "☕"
                markers.add(Marker(r, c, emoji))
            }

            _overlayItems.value = cells
            _markers.value = markers
            _legend.value = legend
            _isLoading.value = false
        }
    }
}