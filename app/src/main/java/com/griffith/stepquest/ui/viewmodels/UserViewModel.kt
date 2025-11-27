package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.griffith.stepquest.data.UserInformation

// a model to keep track of coins and add coins
class UserViewModel : ViewModel() {

    // holds the user name
    var userName by mutableStateOf("User")
        private set
    // holds the number of coins the user has
    var coinStash by mutableStateOf(30)
        private set
    // holds the number of steps the user did
    var steps by mutableStateOf(0)
        private set

    fun loadUserData(userInfo: UserInformation) {
        userName = userInfo.getUsername() ?: ""
    }
    // add number of coins to the user stash
    fun addCoins(amount: Int) {
        coinStash += amount
    }
    // update the total steps the user did
    fun updateSteps(newSteps: Int) {
        steps = newSteps
    }

    // update the user name
    fun updateUserName(newUserName: String) {
        userName = newUserName
    }

}