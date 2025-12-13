package com.griffith.stepquest.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

data class Player(
    val uid: String,
    val name: String,
    val steps: Int,
    val rank: String
)


class RankViewModel : ViewModel() {

    // firestore and auth instances
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // popup message shown on home screen if weekly reward is due
    var weeklyResult by mutableStateOf<String?>(null)
        private set

    // last date the popup was shown
    var lastWeeklyPopupDate by mutableStateOf("")
        private set
    // reset popup
    fun clearWeeklyResult() {
        weeklyResult = null
    }
    // list of players in the same rank as the user (leaderboard)
    var players by mutableStateOf<List<Player>>(emptyList())
        private set

    // load from user document the last time popup was shown
    fun loadWeeklyPopupDate(done: () -> Unit) {
        val user = auth.currentUser
        if (user == null) {
            done()
            return
        }

        db.collection("users")
            .document(user.uid)
            .get()
            .addOnSuccessListener { doc ->
                lastWeeklyPopupDate = doc.getString("lastWeeklyPopupDate") ?: ""
                done()
            }
    }

    // save the last popup date to the user document
    fun saveWeeklyPopupDate(date: String) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        lastWeeklyPopupDate = date

        db.collection("users")
            .document(user.uid)
            .set(
                mapOf("lastWeeklyPopupDate" to date),
                com.google.firebase.firestore.SetOptions.merge()
            )
    }

    // save user weekly performance to leaderboard collection
    fun saveToLeaderboard(name: String, weeklySteps: Int, rank: String) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val data = HashMap<String, Any>()
        data["uid"] = user.uid
        data["name"] = name
        data["weeklysteps"] = weeklySteps
        data["rank"] = rank

        val ref = db.collection("leaderboard").document(user.uid)

        ref.set(data)
    }

    // load all players from this rank to display leaderboard
    fun loadRankPlayers(currentRank: String) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val ref = db.collection("leaderboard")


        ref.whereEqualTo("rank", currentRank)
            .get()
            .addOnSuccessListener { docs ->

                val list = ArrayList<Player>()
                // go through all documents and convert them to Player objects
                for (doc in docs) {

                    val uidValue = doc.getString("uid")
                    if (uidValue == null) {
                        continue
                    }

                    val nameValue = doc.getString("name")
                    if (nameValue == null) {
                        continue
                    }

                    val stepsLong = doc.getLong("weeklysteps")
                    val stepsValue = if (stepsLong == null) 0 else stepsLong.toInt()

                    val rankValue = doc.getString("rank")
                    if (rankValue == null) {
                        continue
                    }

                    val player = Player(uidValue, nameValue, stepsValue, rankValue)
                    list.add(player)
                }

                // sort by steps descending to have the users ranked
                list.sortByDescending { it.steps }

                players = list
            }
    }
    fun removeUserFromLeaderboard() {
        val user = auth.currentUser ?: return

        db.collection("leaderboard")
            .whereEqualTo("uid", user.uid)
            .get()
            .addOnSuccessListener { docs ->
                for (doc in docs) {
                    doc.reference.delete()
                }
            }
    }
    // check weekly rank on Monday and give rewards
    fun weeklyRankCheck(userVM: UserViewModel, coinsVM: CoinsViewModel) {

        // get today's date in a simple format
        val sdf = java.text.SimpleDateFormat("dd-MM-yyyy", java.util.Locale.getDefault())
        val today = sdf.format(java.util.Date())

        // check if already showed popup today
        if (lastWeeklyPopupDate == today) {
            weeklyResult = null
            return
        }

        // check if today is Monday (weekly reset)
        val cal = java.util.Calendar.getInstance()
        val isMonday = (cal.get(java.util.Calendar.DAY_OF_WEEK) == java.util.Calendar.MONDAY)

        // check rank results every monday
        if (!isMonday) {
            weeklyResult = null
            return
        }

        // check which groupd the user belongs to by comparing to his rank
        val currentRank = userVM.userRank

        db.collection("leaderboard")
            .whereEqualTo("rank", currentRank)
            .get()
            .addOnSuccessListener { docs ->

                // build a sorted list of players in the same rank
                val players = docs.map { doc ->
                    Player(
                        uid = doc.getString("uid") ?: "",
                        name = doc.getString("name") ?: "",
                        steps = doc.getLong("weeklysteps")?.toInt() ?: 0,
                        rank = currentRank
                    )
                }.sortedByDescending { it.steps }

                val userId = userVM.userUID
                val position = players.indexOfFirst { it.uid == userId }

                // if the user not found on leaderboard return nll
                if (position == -1) {
                    saveWeeklyPopupDate(today)
                    weeklyResult = null
                    return@addOnSuccessListener
                }

                // first place rewards
                if (position == 0) {
                    userVM.giveMedal(currentRank, "Gold")
                    userVM.promoteRank()
                    userVM.addUserExperience(1000)
                    coinsVM.addCoins(50)
                    saveWeeklyPopupDate(today)
                    weeklyResult = "Gold|EXP:1000|COINS:50|RANKUP:${userVM.userRank}"
                    removeUserFromLeaderboard()
                    return@addOnSuccessListener
                }

                // second place reward
                if (position == 1) {
                    userVM.giveMedal(currentRank, "Silver")
                    userVM.addUserExperience(500)
                    coinsVM.addCoins(25)
                    saveWeeklyPopupDate(today)
                    weeklyResult = "Silver|EXP:500|COINS:25"
                    removeUserFromLeaderboard()
                    return@addOnSuccessListener
                }

                // third place reward
                if (position == 2) {
                    userVM.giveMedal(currentRank, "Bronze")
                    userVM.addUserExperience(250)
                    coinsVM.addCoins(10)
                    saveWeeklyPopupDate(today)
                    weeklyResult = "Bronze|EXP:250|COINS:10"
                    removeUserFromLeaderboard()
                    return@addOnSuccessListener
                }

                // everyone else not in the top 3
                saveWeeklyPopupDate(today)
                val user_pos =  position+1
                weeklyResult = "LOSER|POS:$user_pos"

                removeUserFromLeaderboard()
            }
    }
}
