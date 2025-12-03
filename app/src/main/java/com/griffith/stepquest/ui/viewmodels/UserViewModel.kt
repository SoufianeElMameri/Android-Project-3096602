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
class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    // holds the user name
    var userName by mutableStateOf("User")
        private set

    var userRank by mutableStateOf("Bronze")
        private set

    var userExperience by mutableStateOf(0)
        private set

    fun getUsername(): String {
        return userName
    }

    // update the user name
    fun updateUserName(newUserName: String) {
        userName = newUserName

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .update("username", userName)
    }


    fun updateUserRank(newRank: String) {
        userRank = newRank

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .update("userRank", userRank)
    }

    fun addUserExperience(amount: Int) {
        userExperience += amount

        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .update("userExperience", userExperience)
    }


    // function that loads user data from the database
    fun loadUserData() {
        val user = auth.currentUser
        if (user == null) {
            return
        }
        val mainRef = db.collection("users").document(user.uid)

        mainRef.get().addOnSuccessListener { doc ->
            val name = doc.getString("username")
            if (name == null) {
                userName = ""
            } else {
                userName = name
            }
            val userExperienceValue = doc.getLong("userExperience")
            if (userExperienceValue != null) {
                userExperience = userExperienceValue.toInt()
            }

            val userRankValue = doc.getString("userRank")
            if (userRankValue != null) {
                userRank = userRankValue
            }
        }
    }

}