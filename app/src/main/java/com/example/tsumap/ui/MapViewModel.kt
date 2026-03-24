package com.example.tsumap.ui

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.tsumap.data.map.MapRepository
import androidx.lifecycle.ViewModel

class MapViewModel(
    private val repository: MapRepository
) : ViewModel() {
}