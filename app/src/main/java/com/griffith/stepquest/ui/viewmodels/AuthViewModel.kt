package com.griffith.stepquest.ui.viewmodels

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class AuthViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()

    var usernameError by mutableStateOf("")
        private set

    var emailError by mutableStateOf("")
        private set

    var pwdError by mutableStateOf("")
        private set

    var pwdConfirmError by mutableStateOf("")
        private set

    var authSuccess by mutableStateOf(false)
        private set

    fun resetErrors() {
        usernameError = ""
        emailError = ""
        pwdError = ""
        pwdConfirmError = ""
    }

    fun register(username: String, email: String, password: String, confirm: String) {
        resetErrors()

        if (username.isBlank()) {
            usernameError = "Username required"
            return
        }

        if (email.isBlank()) {
            emailError = "Email required"
            return
        }

        if (password.isBlank()) {
            pwdError = "Password required"
            return
        }

        if (confirm.isBlank()) {
            pwdConfirmError = "Confirm your password"
            return
        }

        if (password != confirm) {
            pwdConfirmError = "Passwords do not match"
            return
        }

        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                val user = auth.currentUser
                if (user == null) {
                    return@addOnSuccessListener
                }

                val uid = user.uid
                db.collection("users")
                    .document(uid)
                    .set(
                        mapOf(
                            "username" to username,
                            "coins" to 0,
                            "userRank" to "Bronze",
                            "userExperience" to 0,
                            "dailyGoal" to 6000,
                            "currentStreak" to 0,
                            "bestStreak" to 0,
                            "lastStreakDate" to ""
                        )
                    )
                authSuccess = true
            }
            .addOnFailureListener { e ->
                val msg = e.message
                var lower = ""
                if (msg != null) {
                    lower = msg.lowercase()
                }
                when {
                    "password" in lower -> pwdError = e.message!!
                    "email" in lower -> emailError = e.message!!
                    "user" in lower && "record" in lower -> emailError = "No account found with this email"
                    else -> pwdConfirmError = e.message!!
                }
            }
    }

    fun login(email: String, password: String) {
        resetErrors()

        if (email.isBlank()){
            emailError = "Email required"
            return
        }
        if (password.isBlank()){
            pwdError = "Password required"
            return
        }

        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                authSuccess = true
            }
            .addOnFailureListener { e ->
                val msg = e.message
                var lower = ""
                if (msg != null) {
                    lower = msg.lowercase()
                }
                when {
                    "password" in lower -> pwdError = e.message!!
                    "email" in lower -> emailError = e.message!!
                    "user" in lower && "record" in lower -> emailError = "No account found with this email"
                    else -> pwdConfirmError = e.message!!
                }
            }
    }
}
