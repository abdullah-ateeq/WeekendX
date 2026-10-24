package com.example.weekendx

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.weekendx.navigation.WeekendXNavHost
import com.example.weekendx.ui.home.WeekendXViewModel
import com.example.weekendx.ui.theme.WeekendXTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val viewModel: WeekendXViewModel = viewModel()
            val uiState by viewModel.uiState.collectAsState()

            WeekendXTheme(
                themeMode = uiState.settings.themeMode,
                dynamicColor = uiState.settings.dynamicColor
            ) {
                Surface(modifier = Modifier.fillMaxSize()) {
                    WeekendXNavHost(viewModel = viewModel)
                }
            }
        }
    }
}
