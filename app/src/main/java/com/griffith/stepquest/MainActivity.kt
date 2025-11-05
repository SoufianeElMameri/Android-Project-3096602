package com.griffith.stepquest


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import com.griffith.stepquest.ui.screens.HomeScreen
import com.griffith.stepquest.ui.screens.ChallengesScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.graphics.toArgb


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tell Android to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar white with dark icons
        window.statusBarColor = Color.White.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            ChallengesScreen()
        }
    }
}


@Preview
@Composable
fun RedText(){
    Text("hello", color = Color.Red)
}

