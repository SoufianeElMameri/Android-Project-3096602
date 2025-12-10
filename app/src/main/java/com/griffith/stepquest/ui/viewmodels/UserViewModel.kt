package com.griffith.stepquest.ui.viewmodels

import android.content.Context
import android.util.Log
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
import kotlin.text.set

// a model to keep track of coins and add coins
class UserViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    val userUID: String
        get() = auth.currentUser?.uid ?: ""

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

    var userNextLevelExp by mutableStateOf(0)
        private set

    fun getUsername(): String {
        return userName
    }

    var userLevel by mutableStateOf(1)
        private set

    var userTitle by mutableStateOf("")
        private set

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

    fun updateStreak(yesterdaySteps: Int, dailyGoal: Int) {

        Log.d("STREAK", "updateStreak called")
        Log.d("STREAK", "yesterdaySteps=$yesterdaySteps dailyGoal=$dailyGoal")

        val user = auth.currentUser
        if (user == null) {
            Log.d("STREAK", "User is null, returning")
            return
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val cal = java.util.Calendar.getInstance()
        cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
        val yesterdayString = sdf.format(cal.time)

        Log.d("STREAK", "yesterdayString=$yesterdayString lastStreakDate=$lastStreakDate")

        if (lastStreakDate == yesterdayString) {
            Log.d("STREAK", "Already updated for yesterday, returning")
            return
        }

        if (yesterdaySteps >= dailyGoal) {

            Log.d("STREAK", "Goal met")

            val prevCal = java.util.Calendar.getInstance()
            prevCal.time = cal.time
            prevCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            val previousDayString = sdf.format(prevCal.time)

            Log.d("STREAK", "previousDayString=$previousDayString")

            if (lastStreakDate == previousDayString) {
                currentStreak = currentStreak + 1
                Log.d("STREAK", "Streak incremented to $currentStreak")
            } else {
                currentStreak = 1
                Log.d("STREAK", "Streak reset to 1")
            }

            lastStreakDate = yesterdayString
            Log.d("STREAK", "lastStreakDate updated to $lastStreakDate")

        } else {
            currentStreak = 0
            Log.d("STREAK", "Goal failed, streak reset to 0")
        }

        if (currentStreak > bestStreak) {
            bestStreak = currentStreak
            Log.d("STREAK", "New bestStreak=$bestStreak")
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

        Log.d("STREAK", "Firestore updated")
    }

    fun loadUserLevel(context: Context, expVM: ExpViewModel) {
        expVM.loadLocal(context) {
            val pair = expVM.getLevelAndTitle(userExperience)
            userLevel = pair.first
            userTitle = pair.second
            userNextLevelExp = pair.third
            Log.d("EXP_DEBUG", "USER RESULTS → Level=$userLevel Title=$userTitle")
        }
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

    fun saveToLeaderboard(uid: String, name: String, weeklySteps: Int, rank: String) {

        val data = hashMapOf(
            "uid" to uid,
            "name" to name,
            "weeklysteps" to weeklySteps,
            "rank" to rank
        )

        db.collection("leaderboard")
            .document(uid)
            .set(data)
    }

}