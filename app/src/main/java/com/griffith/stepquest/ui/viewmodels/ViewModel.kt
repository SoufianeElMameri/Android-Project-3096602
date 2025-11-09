package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel

// a model to keep track of coins and add coins
class ViewModel : ViewModel() {

    var coins by mutableStateOf(30)
        private set

    fun addCoins(amount: Int) {
        coins += amount
    }
}