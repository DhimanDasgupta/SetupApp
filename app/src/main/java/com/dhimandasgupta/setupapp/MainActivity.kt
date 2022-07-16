package com.dhimandasgupta.setupapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dhimandasgupta.setupapp.ui.theme.SetupAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SetupAppTheme { CounterScreen() }
        }
    }
}
