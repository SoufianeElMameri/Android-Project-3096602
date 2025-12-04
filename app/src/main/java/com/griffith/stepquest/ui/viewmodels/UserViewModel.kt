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

    var currentStreak by mutableStateOf(0)
        private set

    var bestStreak by mutableStateOf(0)
        private set

    var lastStreakDate by mutableStateOf("")
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

    fun updateStreak(todaySteps: Int, dailyGoal: Int) {

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())

        val parsedToday = sdf.parse(today)
        if (parsedToday == null) {
            return
        }

        val cal = java.util.Calendar.getInstance()
        cal.time = parsedToday
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterday = sdf.format(cal.time)

        var streakBroken = false
        if (lastStreakDate != today && lastStreakDate != yesterday) {
            streakBroken = true
        }

        if (streakBroken) {
            currentStreak = 0
        }

        var hitGoal = false
        if (todaySteps >= dailyGoal) {
            hitGoal = true
        }

        if (!hitGoal) {
            return
        }

        if (lastStreakDate == yesterday) {
            currentStreak = currentStreak + 1
        } else {
            currentStreak = 1
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
        }

        lastStreakDate = today

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val ref = db.collection("users").document(user.uid)

        ref.set(
            mapOf(
                "currentStreak" to currentStreak,
                "bestStreak" to bestStreak,
                "lastStreakDate" to lastStreakDate
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )
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
            val currentStreakValue = doc.getLong("currentStreak")
            if (currentStreakValue != null) {
                currentStreak = currentStreakValue.toInt()
            }

            val bestStreakValue = doc.getLong("bestStreak")
            if (bestStreakValue != null) {
                bestStreak = bestStreakValue.toInt()
            }

            val lastDateValue = doc.getString("lastStreakDate")
            if (lastDateValue != null) {
                lastStreakDate = lastDateValue
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