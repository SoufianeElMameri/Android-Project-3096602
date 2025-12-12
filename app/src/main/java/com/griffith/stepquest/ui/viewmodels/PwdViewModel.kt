package com.griffith.stepquest.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth

class PwdViewModel : ViewModel() {

    // variables for error messages
    var oldPasswordError by mutableStateOf("")
        private set

    var newPasswordError by mutableStateOf("")
        private set

    var confirmPasswordError by mutableStateOf("")
        private set

    var passwordChangeSuccess by mutableStateOf(false)
        private set

    private val auth = FirebaseAuth.getInstance()

    // reset all errors and success state
    fun resetErrors() {
        oldPasswordError = ""
        newPasswordError = ""
        confirmPasswordError = ""
        passwordChangeSuccess = false
    }

    // change the current user password
    fun changePassword(
        context: Context,
        oldPass: String,
        newPass: String,
        confirmPass: String
    ) {
        resetErrors()
        // validate data
        if (oldPass.isBlank()) {
            oldPasswordError = "Old password is required"
            return
        }

        if (newPass.isBlank()) {
            newPasswordError = "New password cannot be empty"
            return
        }

        if (confirmPass.isBlank()) {
            confirmPasswordError = "Confirm your password"
            return
        }

        if (newPass != confirmPass) {
            confirmPasswordError = "Passwords do not match"
            return
        }

        val user = auth.currentUser
        if (user == null) {
            oldPasswordError = "User not logged in"
            return
        }

        val email = user.email
        if (email == null) {
            oldPasswordError = "No email found for this user"
            return
        }

        // create credential using old password
        val credential = EmailAuthProvider.getCredential(email, oldPass)

        // authenticate user before changing password
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPass)
                    .addOnSuccessListener {
                        passwordChangeSuccess = true
                    }
                    .addOnFailureListener { e ->
                        newPasswordError = e.message ?: "Password update failed"
                    }
            }
            .addOnFailureListener {
                oldPasswordError = "Old password is incorrect"
            }
    }
}
