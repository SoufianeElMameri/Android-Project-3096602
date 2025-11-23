// Soufiane El Mameri 3096602
// GIT REPO LINK: https://github.com/SoufianeElMameri/Android-Project-3096602 
package com.griffith.stepquest


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.griffith.stepquest.data.StepCounter
import com.griffith.stepquest.ui.screens.BadgesScreen
import com.griffith.stepquest.ui.screens.ProfileScreen
import com.griffith.stepquest.ui.screens.RankScreen
import com.griffith.stepquest.ui.screens.ShopScreen
import com.griffith.stepquest.ui.viewmodels.UserViewModel


class MainActivity : ComponentActivity() {
    private val userVM: UserViewModel by viewModels()
    private lateinit var stepCounter: StepCounter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        stepCounter = StepCounter(this)
        // Tell Android to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar white with dark icons
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            StepQuestNav(userVM)
//            HomeScreen()
        }
    }
    override fun onResume() {
        super.onResume()
        if (stepCounter.hasStepCounterSensor()) {
            stepCounter.start()
        }
    }

    override fun onPause() {
        super.onPause()
        stepCounter.stop()
    }
}

// navigation container
@Composable
fun StepQuestNav(userVM: UserViewModel) {
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
            composable("home") { HomeScreen(navController, userVM) }
            composable("challenges") { ChallengesScreen(navController, userVM) }
            composable("badges") { BadgesScreen(navController) }
            composable("rank") { RankScreen(navController = navController) }
            composable("profile") { ProfileScreen() }
            composable("shop"){ ShopScreen(navController, userVM) }
        }
    }
}
