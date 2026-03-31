package com.example.tsumap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tsumap.data.map.MapRepository
import com.example.tsumap.ui.MapScreen
import com.example.tsumap.ui.MapViewModel


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val repository = MapRepository(this)
        val viewModel = MapViewModel(repository)

        setContent {
            MapScreen(viewModel)
        }
    }
}