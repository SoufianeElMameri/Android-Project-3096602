package com.griffith.stepquest.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.griffith.stepquest.ui.screens.Badge
import com.griffith.stepquest.utils.IconsMapping

// view model that checks, award and providing badge data
class BadgeViewModel : ViewModel() {

    // firebase auth
    private val auth = FirebaseAuth.getInstance()

    // function that checks all badges conditions based on user and steps data
    fun checkBadges(userVM: UserViewModel, stepsVM: StepsViewModel) {
        // get currently logged in user
        val user = auth.currentUser
        if (user == null) {
            return
        }
        // current step count for today
        val todaySteps = stepsVM.steps
        // current active streak
        val currentStreak = userVM.currentStreak

        // badge for walking late at night after 9pm
        if (todaySteps >= 2000 && isAfterNinePM()) {
            userVM.giveBadge("NightWalker")
        }
        // badge for walking early in the morning before 8am
        if (todaySteps >= 2000 && isBeforeEightAM()) {
            userVM.giveBadge("EarlyBird")
        }
        // badge for very high daily step count
        if (todaySteps >= 20000) {
            userVM.giveBadge("Marathoner")
        }
        // badge for maintaining a 7 day streak
        if (currentStreak >= 7) {
            userVM.giveBadge("ConsistencyKing")
        }
        // badge for hitting 10000 steps on weekend
        if (isWeekend() && todaySteps >= 10000) {
            userVM.giveBadge("WeekendWarrior")
        }
        // badge for maintaining a 30 day streak
        if (currentStreak >= 30) {
            userVM.giveBadge("StreakMaster")
        }
        // badge for beating daily goal by 50%
        val goal = stepsVM.dailyGoal
        if (todaySteps >= goal * 1.5) {
            userVM.giveBadge("GoalCrusher")
        }
    }
    // function that checks if the current time is before 9 pm
    private fun isAfterNinePM(): Boolean {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.HOUR_OF_DAY) >= 21
    }
    // function that checks if the current time is before 8 am
    private fun isBeforeEightAM(): Boolean {
        val cal = java.util.Calendar.getInstance()
        return cal.get(java.util.Calendar.HOUR_OF_DAY) < 8
    }
    // function that checks if today is a weekend
    private fun isWeekend(): Boolean {
        val cal = java.util.Calendar.getInstance()
        val day = cal.get(java.util.Calendar.DAY_OF_WEEK)
        return day == java.util.Calendar.SATURDAY || day == java.util.Calendar.SUNDAY
    }


    fun getAllBadges(obtained: List<String>): List<Badge> {
        val list = ArrayList<Badge>()
        // badges definitions
        val badgeInfo = listOf(
            Triple("NightWalker", "Walk 2,000 steps after 9:00 PM.", "NightWalker"),
            Triple("EarlyBird", "Walk 2,000 steps before 8:00 AM.", "EarlyBird"),
            Triple("Marathoner", "Walk 20,000 total steps in a day.", "Marathoner"),
            Triple("ConsistencyKing", "Hit your step goal 7 days in a row.", "ConsistencyKing"),
            Triple("WeekendWarrior", "Walk 10,000 steps on a Saturday or Sunday.", "WeekendWarrior"),
            Triple("StreakMaster", "Maintain a 30-day active streak.", "StreakMaster"),
            Triple("GoalCrusher", "Surpass your daily step goal by 50%.", "GoalCrusher")
        )

        // build badge list with obtained status
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
