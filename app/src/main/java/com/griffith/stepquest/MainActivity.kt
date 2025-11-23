// Soufiane El Mameri 3096602
// GIT REPO LINK: https://github.com/SoufianeElMameri/Android-Project-3096602 
package com.griffith.stepquest


import android.os.Build
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
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresApi

class MainActivity : ComponentActivity() {
    private val userVM: UserViewModel by viewModels()
    private lateinit var stepCounter: StepCounter

    private val handler = Handler(Looper.getMainLooper())

    // to keep updating the viewmodel

    private val stepUpdateRunnable = object : Runnable {

        override fun run() {
            Log.d("Main", "Updating viewmodel with steps = ${stepCounter.currentSteps}")
            userVM.updateSteps(stepCounter.currentSteps)
            handler.postDelayed(this, 1000) // update every 1 second
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestStepPermission() {
        if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION)
            != android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(arrayOf(android.Manifest.permission.ACTIVITY_RECOGNITION), 1001)
        }
    }
    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestStepPermission()

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
        if (checkSelfPermission(android.Manifest.permission.ACTIVITY_RECOGNITION)
            == android.content.pm.PackageManager.PERMISSION_GRANTED
        ) {
            if (stepCounter.hasStepCounterSensor()) {
                Log.d("Main", "Device has stepCounter")
                stepCounter.start()
                handler.post(stepUpdateRunnable)
            }
            else{
                Log.d("Main", "Device doesn't have stepCounter")
            }
        }
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(stepUpdateRunnable)
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
