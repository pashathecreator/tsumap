package com.example.tsumap.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.map.MapRepository
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async

class MapViewModel(private val repository: MapRepository) : ViewModel() {

    val grid: Deferred<Array<IntArray>> = viewModelScope.async(Dispatchers.IO) {
        repository.getGrid()
    }
}