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

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    var players by mutableStateOf<List<Player>>(emptyList())
        private set

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

        val ref = db.collection("leaderboard")
            .document(user.uid)

        ref.set(data)
    }

    fun loadRankPlayers(currentName: String, currentRank: String, currentWeeklySteps: Int) {

        val user = auth.currentUser
        if (user == null) {
            return
        }

        val ref = db.collection("leaderboard")

        ref.whereEqualTo("rank", currentRank)
            .get()
            .addOnSuccessListener { docs ->

                val list = ArrayList<Player>()

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
                    val stepsValue: Int

                    if (stepsLong == null) {
                        stepsValue = 0
                    } else {
                        stepsValue = stepsLong.toInt()
                    }

                    val rankValue = doc.getString("rank")
                    if (rankValue == null) {
                        continue
                    }

                    val player = Player(uidValue, nameValue, stepsValue, rankValue)
                    list.add(player)
                }

                val currentPlayer = Player(
                    uid = user.uid,
                    name = currentName,
                    steps = currentWeeklySteps,
                    rank = currentRank
                )

                list.add(currentPlayer)

                list.sortByDescending { it.steps }

                players = list
            }
    }

}
