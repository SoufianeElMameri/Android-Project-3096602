package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.auth.User
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.get

// a model to keep track of coins and add coins
class StepsViewModel : ViewModel() {

    // variable to keep track of the last save time
    private var lastSave = 0L

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // variables to store stats
    var steps by mutableIntStateOf(0)
        private set

    var dailyGoal by mutableIntStateOf(6000)
        private set

    var totalSteps by mutableIntStateOf(0)
        private set

    var weeklySteps by mutableIntStateOf(0)
        private set

    var monthlySteps by mutableIntStateOf(0)
        private set

    var weeklyHistory by mutableStateOf<Map<String, Int>>(emptyMap())
        private set

    // update the total steps the user did
    fun updateSteps(newSteps: Int) {
        steps = newSteps
        val now = System.currentTimeMillis()

        if (now - lastSave >= 30000) {
            saveDailySteps(getToday(), steps)
            lastSave = now
        }
    }

    // function to load the total suer steps
    fun loadTotalSteps() {

        val user = auth.currentUser
        if (user == null) {
            return
        }

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
                    .set(
                        mapOf("total_steps" to totalSteps),
                        com.google.firebase.firestore.SetOptions.merge()
                    )
            }
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

    // function to load the user's steps from the database
    fun loadUserStepsFromDb(onDone: (() -> Unit)? = null) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

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
        onDone?.invoke()
    }
    // function to load the user's step goals
    fun loadDailyStepGoal(userVM : UserViewModel) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val mainRef = db.collection("users").document(user.uid)

        mainRef.get().addOnSuccessListener { doc ->

            val goalValue = doc.getLong("dailyGoal")

            if (goalValue != null) {
                // automatically increment the user step goal based on his level
                dailyGoal = goalValue.toInt() + (500 * userVM.userLevel )
            } else {
                dailyGoal = 3000
            }
        }
    }
    // load total steps for the current month
    fun loadMonthlySteps() {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val cal = java.util.Calendar.getInstance()
        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

        val monthDates = ArrayList<String>()

        // start from first day of the month
        cal.set(java.util.Calendar.DAY_OF_MONTH, 1)

        val targetMonth = cal.get(java.util.Calendar.MONTH)
        val targetYear = cal.get(java.util.Calendar.YEAR)

        // collect all dates in the current month
        while (cal.get(java.util.Calendar.MONTH) == targetMonth && cal.get(java.util.Calendar.YEAR) == targetYear) {
            monthDates.add(sdf.format(cal.time))
            cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
        }

        // split the month dates into two lists because firestore whereIn only allows max 30 items
        val firstBatch = monthDates.take(30)
        val secondBatch = if (monthDates.size > 30) monthDates.drop(30) else emptyList()

        var sum = 0

        val stepsRef = db.collection("users")
            .document(user.uid)
            .collection("daily_steps")

        stepsRef.whereIn("__name__", firstBatch)
            .get()
            .addOnSuccessListener { docs1 ->

                for (doc in docs1) {
                    val s = doc.getLong("steps")?.toInt() ?: 0
                    sum += s
                }

                if (secondBatch.isEmpty()) {
                    monthlySteps = sum
                    return@addOnSuccessListener
                }

                stepsRef.whereIn("__name__", secondBatch)
                    .get()
                    .addOnSuccessListener { docs2 ->

                        for (doc in docs2) {
                            val s = doc.getLong("steps")?.toInt() ?: 0
                            sum += s
                        }

                        monthlySteps = sum
                    }
            }
    }
    // function that loads the current week's stats and the total weekly steps
    fun loadWeeklyHistory(saveToDb: Boolean = false, rankedViewModel: RankViewModel? = null, userViewModel: UserViewModel? = null) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val cal = java.util.Calendar.getInstance()
        cal.firstDayOfWeek = java.util.Calendar.MONDAY
        cal.set(java.util.Calendar.DAY_OF_WEEK, java.util.Calendar.MONDAY)

        val sdf = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
        val dayFormat = SimpleDateFormat("EEE", Locale.getDefault())

        val weekDates = ArrayList<String>()
        val dayNames = LinkedHashMap<String, String>()

        // build list of week dates and day names
        for (i in 0 until 7) {
            val dateString = sdf.format(cal.time)
            val dayName = dayFormat.format(cal.time)
            dayNames[dayName] = dateString
            weekDates.add(dateString)
            cal.add(java.util.Calendar.DAY_OF_YEAR, 1)
        }

        val stepsRef = db.collection("users")
            .document(user.uid)
            .collection("daily_steps")

        stepsRef.whereIn("__name__", weekDates)
            .get()
            .addOnSuccessListener { docs ->

                val result = LinkedHashMap<String, Int>()
                // initialize days with zero steps
                for ((day, dateStr) in dayNames) {
                    result[day] = 0
                }

                // fill in available steps for each day
                for (doc in docs) {
                    val dateStr = doc.id
                    val stepsValue = doc.getLong("steps")?.toInt() ?: 0

                    for ((day, d) in dayNames) {
                        if (d == dateStr) {
                            result[day] = stepsValue
                        }
                    }
                }

                weeklyHistory = result

                weeklySteps = result.values.sum()

                // option to save weekly steps and update the leaderboard
                if (saveToDb && rankedViewModel != null && userViewModel != null)  {
                    val userRef = db.collection("users")
                        .document(user.uid)

                    userRef.set(
                        mapOf(
                            "weeklySteps" to weeklySteps
                        ),
                        com.google.firebase.firestore.SetOptions.merge()
                    )

                    rankedViewModel.saveToLeaderboard(
                        userViewModel.userName,
                        weeklySteps,
                        userViewModel.userRank
                    )


                }
        }

    }


    // function to load all necessary stats when the app starts
    fun loadStepsStats(userVM : UserViewModel){
        loadDailyStepGoal(userVM)
        loadTotalSteps()
        loadMonthlySteps()
        loadWeeklyHistory(true)
    }

}