package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.griffith.stepquest.data.UserInformation
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.get

// a model to keep track of coins and add coins
class CoinsViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // holds the number of coins the user has
    var coinStash by mutableStateOf(0)
        private set


    // add number of coins to the user stash
    fun addCoins(amount: Int) {
        coinStash += amount

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .update("coins", coinStash)
    }

    // add number of coins to the user stash
    fun useCoins(amount: Int) {
        coinStash -= amount

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .update("coins", coinStash)
    }

    // function to get today's date
    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    // function that loads user data from the database
    fun loadCoins() {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val mainRef = db.collection("users").document(user.uid)

        mainRef.get().addOnSuccessListener { doc ->

            val coinsValue = doc.getLong("coins")
            if (coinsValue != null) {
                coinStash = coinsValue.toInt()
            }

        }
    }


}