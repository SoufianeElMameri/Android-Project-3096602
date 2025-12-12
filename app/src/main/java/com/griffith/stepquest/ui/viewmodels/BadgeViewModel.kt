package com.griffith.stepquest.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.griffith.stepquest.ui.screens.Badge
import com.griffith.stepquest.utils.IconsMapping

class BadgeViewModel : ViewModel() {

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    fun checkBadges(userVM: UserViewModel, stepsVM: StepsViewModel) {
        val user = auth.currentUser
        if (user == null) {
            return
        }

        val totalSteps = stepsVM.totalSteps
        val todaySteps = stepsVM.steps
        val currentStreak = userVM.currentStreak

        if (todaySteps >= 2000 && isAfterNinePM()) {
            userVM.giveBadge("NightWalker")
        }

        if (todaySteps >= 2000 && isBeforeEightAM()) {
            userVM.giveBadge("EarlyBird")
        }

        if (todaySteps >= 20000) {
            userVM.giveBadge("Marathoner")
        }

        if (currentStreak >= 7) {
            userVM.giveBadge("ConsistencyKing")
        }

        if (isWeekend() && todaySteps >= 10000) {
            userVM.giveBadge("WeekendWarrior")
        }

        if (hasVisitedFiveLocations()) {
            userVM.giveBadge("Explorer")
        }

        if (currentStreak >= 30) {
            userVM.giveBadge("StreakMaster")
        }

        val goal = stepsVM.dailyGoal
        if (todaySteps >= goal * 1.5) {
            userVM.giveBadge("GoalCrusher")
        }
    }

    private fun isAfterNinePM(): Boolean {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.HOUR_OF_DAY) >= 21
    }

    private fun isBeforeEightAM(): Boolean {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.HOUR_OF_DAY) < 8
    }

    private fun isWeekend(): Boolean {
        val cal = java.util.Calendar.getInstance()
        val day = cal.get(java.util.Calendar.DAY_OF_WEEK)
        return day == java.util.Calendar.SATURDAY || day == java.util.Calendar.SUNDAY
    }

    private fun hasVisitedFiveLocations(): Boolean {
        return false
    }

    fun getAllBadges(obtained: List<String>): List<Badge> {
        val list = ArrayList<Badge>()

        val badgeInfo = listOf(
            Triple("NightWalker", "Walk 5,000 steps after 9:00 PM.", "NightWalker"),
            Triple("EarlyBird", "Walk 5,000 steps before 8:00 AM.", "EarlyBird"),
            Triple("Marathoner", "Walk 42,000 total steps in a day.", "Marathoner"),
            Triple("ConsistencyKing", "Hit your step goal 7 days in a row.", "ConsistencyKing"),
            Triple("WeekendWarrior", "Walk 10,000 steps on a Saturday or Sunday.", "WeekendWarrior"),
            Triple("Explorer", "Walk in 5 different locations in a week.", "Explorer"),
            Triple("StreakMaster", "Maintain a 30-day active streak.", "StreakMaster"),
            Triple("GoalCrusher", "Surpass your daily step goal by 50%.", "GoalCrusher")
        )

        for (item in badgeInfo) {
            val name = item.first
            val desc = item.second
            val key = item.third
            val icon = IconsMapping.badgeIcons[key] ?: 0
            val has = obtained.contains(name)
            list.add(Badge(name, desc, icon, has))
        }

        return list
    }
}
