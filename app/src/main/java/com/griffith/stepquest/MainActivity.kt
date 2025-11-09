package com.griffith.stepquest


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color
import com.griffith.stepquest.ui.screens.HomeScreen
import com.griffith.stepquest.ui.screens.ChallengesScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.compose.ui.graphics.toArgb
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.griffith.stepquest.ui.components.BottomNavBar

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import com.griffith.stepquest.ui.screens.BadgesScreen
import com.griffith.stepquest.ui.screens.ProfileScreen
import com.griffith.stepquest.ui.screens.RankScreen
import com.griffith.stepquest.ui.screens.ShopScreen


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Tell Android to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar white with dark icons
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            StepQuestNav()
//            HomeScreen()
        }
    }
}

// navigation container
@Composable
fun StepQuestNav() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { BottomNavBar(navController) }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(paddingValues),
            enterTransition = { EnterTransition.None },
            exitTransition = { ExitTransition.None },
            popEnterTransition = { EnterTransition.None },
            popExitTransition = { ExitTransition.None }
        ) {
            composable("home") { HomeScreen(navController) }
            composable("challenges") { ChallengesScreen(navController) }
            composable("badges") { BadgesScreen(navController) }
            composable("rank") { RankScreen(navController = navController) }
            composable("profile") { ProfileScreen() }
            composable("shop"){ ShopScreen(navController) }
        }
    }
}
