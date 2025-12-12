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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.griffith.stepquest.ui.screens.AuthScreen
import com.griffith.stepquest.ui.screens.SettingsScreen
import com.griffith.stepquest.data.FirebaseAuthManger
import com.griffith.stepquest.ui.screens.PrivacyPolicyScreen
import com.griffith.stepquest.ui.screens.TermsAndConditionsScreen
import com.griffith.stepquest.ui.viewmodels.AuthViewModel
import com.griffith.stepquest.ui.viewmodels.BadgeViewModel
import com.griffith.stepquest.ui.viewmodels.CoinsViewModel
import com.griffith.stepquest.ui.viewmodels.ExpViewModel
import com.griffith.stepquest.ui.viewmodels.PwdViewModel
import com.griffith.stepquest.ui.viewmodels.RankViewModel
import com.griffith.stepquest.ui.viewmodels.StepsViewModel

class MainActivity : ComponentActivity() {

    // VIEW MODELS
    private val userVM: UserViewModel by viewModels()
    private val stepsVM: StepsViewModel by viewModels()
    private val coinsVM: CoinsViewModel by viewModels()
    private val authVM: AuthViewModel by viewModels()
    private val pwdVM: PwdViewModel by viewModels()
    private val expVM: ExpViewModel by viewModels()
    private val rankVM: RankViewModel by viewModels()
    private val badgeVM: BadgeViewModel by viewModels()

    private lateinit var stepCounter: StepCounter

    private val handler = Handler(Looper.getMainLooper())

    // to keep updating the viewmodel
    private val stepUpdateRunnable = object : Runnable {

        override fun run() {
            stepCounter.forceReadSensor()
            // update every 1 second
            handler.postDelayed(this, 3000)
        }
    }

    // request permission from user to use sensor
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
        // call request permission function
        requestStepPermission()

        stepCounter = StepCounter(this, stepsVM, userVM, rankVM)

        // Tell Android to handle system bars
        WindowCompat.setDecorFitsSystemWindows(window, true)

        // Make status bar white with dark icons
        window.statusBarColor = Color.Black.toArgb()
        WindowInsetsControllerCompat(window, window.decorView).isAppearanceLightStatusBars = true

        setContent {
            // state to remember if user is logged in or out (to trigger screen change)
            var isLoggedIn by remember { mutableStateOf(FirebaseAuthManger.isLoggedIn()) }

            // if user is logged in show the app screens
            if (isLoggedIn) {

                LaunchedEffect(Unit) {
                    userVM.loadUserData (this@MainActivity, expVM){
                        coinsVM.loadCoins()
                        stepsVM.loadStepsStats(userVM)
                        rankVM.loadWeeklyPopupDate {
                            rankVM.weeklyRankCheck(userVM, coinsVM)
                        }
                        badgeVM.checkBadges(userVM, stepsVM)
                    }
                }
                
                StepQuestNav(
                    userVM   = userVM,
                    stepsVM  = stepsVM,
                    coinsVM  = coinsVM,
                    pwdVM    = pwdVM,
                    rankVM   = rankVM,
                    badgeVM  = badgeVM,
                    onLogout = {
                        FirebaseAuthManger.logoutUser()
                        isLoggedIn = false
                        // kill all viewmodel by reloading the main activity
                        viewModelStore.clear()
                    }
                )

            // if user is not logged in show the authentication screen
            } else {
                AuthScreen(
                    authVM,
                    onLoginSuccess = {
                        stepsVM.loadUserStepsFromDb()
                        isLoggedIn = true
                    }
                )
            }
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
                stepCounter.forceReadSensor { steps ->
                    stepsVM.updateSteps(steps)
                    userVM.updateStreak(steps, stepsVM.dailyGoal)
                }
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
fun StepQuestNav(userVM: UserViewModel, stepsVM: StepsViewModel, coinsVM: CoinsViewModel, pwdVM: PwdViewModel, rankVM: RankViewModel, badgeVM: BadgeViewModel, onLogout: () -> Unit ) {
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
            composable("home") { HomeScreen(navController, userVM, stepsVM, coinsVM, rankVM) }
            composable("challenges") { ChallengesScreen(navController, userVM, stepsVM, coinsVM) }
            composable("badges") { BadgesScreen(navController, userVM, badgeVM) }
            composable("rank") { RankScreen(navController, userVM, stepsVM, rankVM) }
            composable("profile") { ProfileScreen(navController = navController, userVM, stepsVM) }
            composable("settings") { SettingsScreen(navController,userVM,pwdVM,
                onLogout = {
                    onLogout()
                }
            ) }
            composable("terms") {
                TermsAndConditionsScreen(navController)
            }

            composable("privacy") {
                PrivacyPolicyScreen(navController)
            }
//            composable("shop"){ ShopScreen(navController, userVM) }
        }
    }
}
