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

    private var totalStepsLoaded = false
    private var lastSave = 0L
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()


    // holds the user name
    var userName by mutableStateOf("User")
        private set

    var userRank by mutableStateOf("Bronze")
        private set

    // holds the number of coins the user has
    var coinStash by mutableStateOf(0)
        private set

    // holds the number of steps the user did
    var steps by mutableStateOf(0)
        private set

    var totalSteps by mutableStateOf(0)
        private set

    var userExperience by mutableStateOf(0)
        private set

    var weeklySteps by mutableStateOf(0)
        private set

    var monthlySteps by mutableStateOf(0)
        private set


    fun loadUserData(userInfo: UserInformation) {
        val name = userInfo.getUsername()
        if (name == null) {
            userName = ""
        } else {
            userName = name
        }
    }
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

    // update the total steps the user did
    fun updateSteps(newSteps: Int) {
        steps = newSteps
        val now = System.currentTimeMillis()

        if (now - lastSave >= 30000) {
            saveDailySteps(getToday(), steps)
            lastSave = now
        }
    }

    fun updateUserRank(newRank: String) {
        userRank = newRank

        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .update("userRank", userRank)
    }

    fun loadTotalSteps() {
        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .collection("daily_steps")
            .get()
            .addOnSuccessListener { docs ->
                var sum = 0
                for (doc in docs) {
                    val s = doc.getLong("steps")?.toInt() ?: 0
                    sum += s
                }
                totalSteps = sum

                db.collection("users")
                    .document(user.uid)
                    .update("total_steps", totalSteps)
            }
    }

    fun addUserExperience(amount: Int) {
        userExperience += amount

        val user = auth.currentUser ?: return

        db.collection("users")
            .document(user.uid)
            .update("userExperience", userExperience)
    }

    // update the user name
    fun updateUserName(newUserName: String) {
        userName = newUserName
    }

    // function that saves the total daily steps in the database
    fun saveDailySteps(date: String, value: Int) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val ref = db.collection("users")
            .document(user.uid)
            .collection("daily_steps")
            .document(date)

        val data = HashMap<String, Any>()
        data["steps"] = value

        ref.set(data)
    }

    // function to get today's date
    private fun getToday(): String {
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        return sdf.format(Date())
    }

    // function that loads user data from the database
    fun loadUserData() {
        if(!totalStepsLoaded){
            loadTotalSteps()
            totalStepsLoaded = true
        }

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val today = getToday()

        val mainRef = db.collection("users").document(user.uid)



        mainRef.get().addOnSuccessListener { doc ->
            val name = doc.getString("username")
            if (name == null) {
                userName = ""
            } else {
                userName = name
            }

            val coinsValue = doc.getLong("coins")
            if (coinsValue != null) {
                coinStash = coinsValue.toInt()
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

    // function to load the user's steps from the database
    fun loadUserStepsFromDb() {
        val user = auth.currentUser ?: return

        val today = getToday()
        val mainRef = db.collection("users").document(user.uid)
        val dailyRef = mainRef.collection("daily_steps").document(today)

        dailyRef.get().addOnSuccessListener { doc ->
            val todaySteps = doc.getLong("steps")
            if (todaySteps != null) {
                steps = todaySteps.toInt()
            } else {
                steps = 0
            }
        }

        mainRef.get().addOnSuccessListener { doc ->
            val totalStepsValue = doc.getLong("total_steps")
            if (totalStepsValue != null) {
                totalSteps = totalStepsValue.toInt()
            }
        }
    }

    fun loadWeeklySteps() {
        val user = auth.currentUser ?: return
        val today = Date()
        val cal = java.util.Calendar.getInstance()
        cal.time = today
        cal.set(java.util.Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        db.collection("users").document(user.uid).collection("daily_steps")
            .get()
            .addOnSuccessListener { docs ->
                var sum = 0
                for (doc in docs) {
                    val d = sdf.parse(doc.id) ?: continue
                    if (!d.before(cal.time) && !d.after(today)) {
                        val s = doc.getLong("steps")?.toInt() ?: 0
                        sum += s
                    }
                }
                weeklySteps = sum
            }
    }

    fun loadMonthlySteps() {
        val user = auth.currentUser ?: return
        val today = Date()
        val cal = java.util.Calendar.getInstance()
        cal.time = today
        val month = cal.get(java.util.Calendar.MONTH)
        val year = cal.get(java.util.Calendar.YEAR)
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        db.collection("users").document(user.uid).collection("daily_steps")
            .get()
            .addOnSuccessListener { docs ->
                var sum = 0
                for (doc in docs) {
                    val d = sdf.parse(doc.id) ?: continue
                    val c = java.util.Calendar.getInstance()
                    c.time = d
                    if (c.get(java.util.Calendar.MONTH) == month && c.get(java.util.Calendar.YEAR) == year) {
                        val s = doc.getLong("steps")?.toInt() ?: 0
                        sum += s
                    }
                }
                monthlySteps = sum
            }
    }


}