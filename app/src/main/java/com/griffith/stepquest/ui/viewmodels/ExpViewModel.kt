package com.griffith.stepquest.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.griffith.stepquest.data.UserInformation
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.text.get
import androidx.core.content.edit
import android.util.Log




data class LevelData(
    val level: Int,
    val xp: Int,
    val title: String
)

class ExpViewModel : ViewModel() {

    var levels: List<LevelData> = emptyList()
        private set

    fun loadLevelsFromFirebase(context: Context, onDone: () -> Unit) {
        FirebaseFirestore.getInstance()
            .collection("levels")
            .document("levels_data")
            .get()
            .addOnSuccessListener { doc ->
                Log.d("EXP_DEBUG", "Firestore document loaded")
                val raw = doc.get("levels")
                Log.d("EXP_DEBUG", "raw levels value: $raw")
                if (raw == null) {
                    return@addOnSuccessListener
                }

                val casted = raw as? Map<String, Map<String, Any>>

                Log.d("EXP_DEBUG", "casted map: $casted")

                if (casted == null) {
                    return@addOnSuccessListener
                }

//                val arr = casted


                val list = ArrayList<LevelData>()

                for ((_, value) in casted) {
                    val lvl = value["level"].toString().toInt()
                    val xp = value["xp"].toString().toInt()
                    val title = value["title"].toString()
                    Log.d("EXP_DEBUG", "Parsed level=$lvl xp=$xp title=$title")
                    list.add(LevelData(lvl, xp, title))
                }
                levels = list
                Log.d("EXP_DEBUG", "Final levels list: $levels")
                saveLocal(context, list)
                onDone()
            }
    }

    fun saveLocal(context: Context, list: List<LevelData>) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
        val prefs = context.getSharedPreferences("exp_levels_$uid", Context.MODE_PRIVATE)

        val json = JSONArray()

        for (lvl in list) {
            val jObject = JSONObject()
            jObject.put("level", lvl.level)
            jObject.put("xp", lvl.xp)
            jObject.put("title", lvl.title)
            json.put(jObject)
        }
        prefs.edit { putString("levels_json", json.toString()) }
    }

    fun loadLocal(context: Context, onDone: () -> Unit) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: "default_user"
        val prefs = context.getSharedPreferences("exp_levels_$uid", Context.MODE_PRIVATE)

        val json = prefs.getString("levels_json", null)


        if (json == null) {
            loadLevelsFromFirebase(context, onDone)
            return
        }


        val arr = JSONArray(json)
        val list = ArrayList<LevelData>()
        for (i in 0 until arr.length()) {
            val jObject = arr.getJSONObject(i)
            val lvl = jObject.getInt("level")
            val xp = jObject.getInt("xp")
            val title = jObject.getString("title")
            list.add(LevelData(lvl, xp, title))
        }
        levels = list
        onDone()
    }

    fun getLevelAndTitle(userXp: Int): Triple<Int, String, Int> {
        Log.d("EXP_DEBUG", "getLevelAndTitle called with userXp=$userXp")
        Log.d("EXP_DEBUG", "levels loaded: $levels")
        var resultLevel = 1
        var resultTitle = ""
        var nextLevelXp = 0
        for (i in levels.indices) {
            val lvl = levels[i]
            Log.d("EXP_DEBUG", "Checking level=${lvl.level} xp=${lvl.xp} title=${lvl.title}")

            if (userXp >= lvl.xp) {
                resultLevel = lvl.level
                resultTitle = lvl.title
                if (i + 1 < levels.size) {
                    nextLevelXp = levels[i + 1].xp
                } else {
                    nextLevelXp = lvl.xp
                }
                Log.d("EXP_DEBUG", "MATCH → new result: $resultLevel / $resultTitle")
            }
        }
        Log.d("EXP_DEBUG", "FINAL → level=$resultLevel title=$resultTitle")
        return Triple(resultLevel, resultTitle, nextLevelXp)
    }
}

