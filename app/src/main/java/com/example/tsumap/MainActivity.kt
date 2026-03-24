package com.example.tsumap

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.example.tsumap.ui.MapScreen
import com.example.tsumap.ui.theme.TsuMapTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            TsuMapTheme {
                MapScreen()
            }
        }
    }
}