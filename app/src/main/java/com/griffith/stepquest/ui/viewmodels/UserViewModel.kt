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

    var totalGoldMedals by mutableStateOf(0)
        private set

    var rankMedals by mutableStateOf<Map<String, List<String>>>(emptyMap())
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

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val today = sdf.format(Date())

        val ref = db.collection("users").document(user.uid)

        ref.get().addOnSuccessListener { doc ->

            val lastCheck = doc.getString("lastStreakCheck")
            if (lastCheck == today) {
                return@addOnSuccessListener
            }

            val cal = java.util.Calendar.getInstance()
            cal.add(java.util.Calendar.DAY_OF_YEAR, -1)
            val yesterdayString = sdf.format(cal.time)

            if (lastStreakDate == yesterdayString) {
                ref.set(
                    mapOf("lastStreakCheck" to today),
                    com.google.firebase.firestore.SetOptions.merge()
                )
                return@addOnSuccessListener
            }

            if (yesterdaySteps >= dailyGoal) {

                val prevCal = java.util.Calendar.getInstance()
                prevCal.time = cal.time
                prevCal.add(java.util.Calendar.DAY_OF_YEAR, -1)
                val previousDayString = sdf.format(prevCal.time)

                if (lastStreakDate == previousDayString) {
                    currentStreak = currentStreak + 1
                } else {
                    currentStreak = 1
                }

                lastStreakDate = yesterdayString

            } else {
                currentStreak = 0
            }

            if (currentStreak > bestStreak) {
                bestStreak = currentStreak
            }

            ref.set(
                mapOf(
                    "currentStreak" to currentStreak,
                    "bestStreak" to bestStreak,
                    "lastStreakDate" to lastStreakDate,
                    "lastStreakCheck" to today
                ),
                com.google.firebase.firestore.SetOptions.merge()
            )
        }
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
    fun loadUserData(onDone: (() -> Unit)? = null) {
        val user = auth.currentUser
        if (user == null) {
            onDone?.invoke()
            return
        }

        val mainRef = db.collection("users").document(user.uid)

        mainRef.get()
            .addOnSuccessListener { doc ->

                userName = doc.getString("username") ?: ""
                currentStreak = doc.getLong("currentStreak")?.toInt() ?: 0
                bestStreak = doc.getLong("bestStreak")?.toInt() ?: 0
                lastStreakDate = doc.getString("lastStreakDate") ?: ""
                userExperience = doc.getLong("userExperience")?.toInt() ?: 0
                userRank = doc.getString("userRank") ?: "Bronze"
                loadMedals()
                onDone?.invoke()
            }
            .addOnFailureListener {
                onDone?.invoke()
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

    fun giveMedal(rank: String, medalName: String) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val ref = db.collection("users").document(user.uid)

        ref.get().addOnSuccessListener { doc ->
            val raw = doc.get("rankMedals") as? Map<*, *>
            val updated = HashMap<String, MutableList<String>>()

            if (raw != null) {
                for (entry in raw.entries) {
                    val key = entry.key
                    val value = entry.value

                    if (key is String && value is List<*>) {
                        val clean = value.filterIsInstance<String>().toMutableList()
                        updated[key] = clean
                    }
                }
            }

            val list = updated[rank] ?: mutableListOf()
            list.add(medalName)
            updated[rank] = list

            ref.update("rankMedals", updated)
        }
    }



    fun loadMedals() {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->

                val raw = doc.get("rankMedals") as? Map<*, *>
                val cleanMap = HashMap<String, List<String>>()

                if (raw != null) {
                    for (entry in raw.entries) {
                        val key = entry.key
                        val value = entry.value

                        if (key is String && value is List<*>) {
                            cleanMap[key] = value.filterIsInstance<String>()
                        }
                    }
                }

                rankMedals = cleanMap

                var goldCount = 0
                for (pair in cleanMap.entries) {
                    for (m in pair.value) {
                        if (m == "Gold") {
                            goldCount = goldCount + 1
                        }
                    }
                }

                totalGoldMedals = goldCount
            }
    }


    fun promoteRank() {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val next = when (userRank) {
            "Bronze" -> "Silver"
            "Silver" -> "Gold"
            "Gold" -> "Diamond"
            "Diamond" -> "Legend"
            else -> userRank
        }

        userRank = next

        db.collection("users").document(user.uid)
            .set(mapOf("userRank" to userRank), com.google.firebase.firestore.SetOptions.merge())
    }

}