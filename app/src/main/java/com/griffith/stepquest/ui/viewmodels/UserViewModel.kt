package com.griffith.stepquest.ui.viewmodels

import android.content.Context
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.Calendar

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

    var lastStreak by mutableIntStateOf(0)
        private set

    var currentStreak by mutableIntStateOf(0)
        private set

    var bestStreak by mutableIntStateOf(0)
        private set

    var lastStreakDate by mutableStateOf("")
        private set

    var userRank by mutableStateOf("Bronze")
        private set


    var userExperience by mutableIntStateOf(0)
        private set

    var userNextLevelExp by mutableIntStateOf(0)
        private set

    var totalGoldMedals by mutableIntStateOf(0)
        private set

    var rankMedals by mutableStateOf<Map<String, List<String>>>(emptyMap())
        private set

    var badgesObtained by mutableStateOf<List<String>>(emptyList())
        private set

    fun getUsername(): String {
        return userName
    }

    var userLevel by mutableIntStateOf(1)
        private set

    var userTitle by mutableStateOf("")
        private set

    // holds the streak popup message
    var streakPopupMessage by mutableStateOf<String?>(null)
        private set

    // holds if the streak was broken
    var streakBroken by mutableStateOf(false)
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
    // function that adds an amout of exp to the user's exp
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
    // function that updates the user's streak increment, reset, or break
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

            val cal = Calendar.getInstance()
            cal.add(Calendar.DAY_OF_YEAR, -1)
            val yesterdayString = sdf.format(cal.time)

            if (lastStreakDate == yesterdayString) {
                ref.set(
                    mapOf("lastStreakCheck" to today),
                    com.google.firebase.firestore.SetOptions.merge()
                )
                return@addOnSuccessListener
            }

            if (yesterdaySteps >= dailyGoal) {

                val prevCal = Calendar.getInstance()
                prevCal.add(Calendar.DAY_OF_YEAR, -2)
                val previousDayString = sdf.format(prevCal.time)

                if (lastStreakDate == previousDayString) {
                    currentStreak = currentStreak + 1
                    streakPopupMessage = "Streak increased!!!"
                } else {
                    currentStreak = 1
                    streakPopupMessage = "New streak started!!!"
                }
                streakBroken = false
                lastStreakDate = yesterdayString

            } else if(currentStreak > 0) {
                lastStreak          = currentStreak
                currentStreak       = 0
                streakBroken        = true
                streakPopupMessage  = "Streak broken"
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
    fun repairStreak() {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -1)
        val yesterdayString = sdf.format(cal.time)

        val ref = db.collection("users").document(user.uid)

        currentStreak = lastStreak
        streakBroken = false
        resetStreakMessage()

        ref.set(
            mapOf(
                "currentStreak" to currentStreak,
                "bestStreak" to bestStreak,
                "lastStreakDate" to yesterdayString
            ),
            com.google.firebase.firestore.SetOptions.merge()
        )
    }

    fun resetStreakMessage(){
        streakPopupMessage = null
    }


    // function to load the user's level, title and expeirence to next level
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
    fun loadUserData(context: Context, expVM: ExpViewModel, onDone: (() -> Unit)? = null) {
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
                loadUserLevel(context, expVM)
                loadMedals()
                loadBadges {
                    onDone?.invoke()
                }
            }
            .addOnFailureListener {
                onDone?.invoke()
            }
    }
    // function to give the user a medal
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


    // function that loads the user's obtained medals and calculates the total gold medals obtained
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

    // function that promotes the user to the next rank
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
    // function that loads all badges obtained by the user
    fun loadBadges(onDone: (() -> Unit)? = null) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                val raw = doc.get("badgesObtained")
                if (raw is List<*>) {
                    val clean = ArrayList<String>()
                    for (item in raw) {
                        if (item is String) {
                            clean.add(item)
                        }
                    }
                    badgesObtained = clean
                    onDone?.invoke()
                }
            }
    }

    // function that gives a badge to the user if not already owned
    fun giveBadge(badgeName: String) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val updated = badgesObtained.toMutableList()
        if (!updated.contains(badgeName)) {
            updated.add(badgeName)
        }

        badgesObtained = updated

        db.collection("users")
            .document(user.uid)
            .set(
                mapOf("badgesObtained" to updated),
                com.google.firebase.firestore.SetOptions.merge()
            )
    }

}