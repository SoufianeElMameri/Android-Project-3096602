package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// a model to keep track of coins and add coins
class UserViewModel : ViewModel() {

    // holds the number of coins the user has
    var coinStash by mutableStateOf(30)
        private set
    // holds the number of steps the user did
    var steps by mutableStateOf(0)
        private set

    // add number of coins to the user stash
    fun addCoins(amount: Int) {
        coinStash += amount
    }
    // update the total steps the user did
    fun updateSteps(newSteps: Int) {
        steps = newSteps
    }
}