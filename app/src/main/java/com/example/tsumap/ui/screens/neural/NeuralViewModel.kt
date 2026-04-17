package com.example.tsumap.ui.screens.neural

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tsumap.data.neural.MLPLoader
import com.example.tsumap.data.repository.RatingRepository
import com.example.tsumap.domain.usecase.neural.PredictDigitUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class NeuralViewModel(
    context: Context,
    private val ratingRepository: RatingRepository
) : ViewModel() {

    private val predictUseCase = PredictDigitUseCase(MLPLoader.load(context))

    private val _pixels = MutableStateFlow(Array(28) { FloatArray(28) { 0f } })
    val pixels: StateFlow<Array<FloatArray>> = _pixels

    private val _predictedDigit = MutableStateFlow<Int?>(null)
    val predictedDigit: StateFlow<Int?> = _predictedDigit

    private val _isSaved = MutableStateFlow(false)
    val isSaved: StateFlow<Boolean> = _isSaved

    fun drawPixel(row: Int, col: Int) {
        if (row !in 0..<28 || col < 0 || col >= 28) return
        val updated = _pixels.value.map { it.copyOf() }.toTypedArray()
        for (dr in -1..1) {
            for (dc in -1..1) {
                val r = row + dr
                val c = col + dc
                if (r in 0..27 && c in 0..27) updated[r][c] = 1f
            }
        }
        _pixels.value = updated
    }

    fun predict() {
        _predictedDigit.value = predictUseCase.invoke(_pixels.value)
    }

    fun clear() {
        _pixels.value = Array(28) { FloatArray(28)  }
        _predictedDigit.value = null
        _isSaved.value = false
    }

    fun saveRating(placeName: String) {
        val digit = _predictedDigit.value ?: return
        viewModelScope.launch {
            ratingRepository.saveRating(placeName, digit)
            _isSaved.value = true
        }
    }
}
