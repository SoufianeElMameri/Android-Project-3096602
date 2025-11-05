package com.griffith.stepquest


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import com.griffith.stepquest.ui.screens.HomeScreen
import com.griffith.stepquest.ui.screens.ChallengesScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tell Android to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar white with dark icons
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            ChallengesScreen()
//            HomeScreen()
        }
    }
}


@Preview
@Composable
fun RedText(){
    Text("hello", color = Color.Red)
}

