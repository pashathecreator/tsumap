package com.example.tsumap.ui.screens.neural

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.repository.PlaceRepository
import com.example.tsumap.data.repository.RatingRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class CafeItem(
    val key: String,
    val name: String,
    val emoji: String
)

class CafeSharedViewModel(
    private val ratingRepository: RatingRepository,
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _cafeList = MutableStateFlow<List<CafeItem>>(emptyList())
    val cafeList: StateFlow<List<CafeItem>> = _cafeList

    private val _selectedCafe = MutableStateFlow<CafeItem?>(null)
    val selectedCafe: StateFlow<CafeItem?> = _selectedCafe

    private val _averageRatings = MutableStateFlow<Map<String, Float?>>(emptyMap())
    val averageRatings: StateFlow<Map<String, Float?>> = _averageRatings

    init {
        viewModelScope.launch {
            val cafes = placeRepository.getCafes().first()
            val items = cafes.map { CafeItem(it.key, it.name, it.emoji) }
            _cafeList.value = items

            items.forEach { cafe ->
                launch {
                    ratingRepository.getAverageRating(cafe.key).collectLatest { avg ->
                        _averageRatings.value += (cafe.key to avg)
                    }
                }
            }
        }
    }

    fun selectCafe(cafe: CafeItem) {
        _selectedCafe.value = cafe
    }
}