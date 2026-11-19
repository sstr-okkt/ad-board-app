package com.example.adboardapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.adboardapp.ui.AdListScreen
import com.example.adboardapp.ui.theme.AdBoardAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AdBoardAppTheme {
                AdListScreen()
            }
        }
    }
}
