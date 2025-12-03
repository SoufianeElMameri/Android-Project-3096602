package com.griffith.stepquest.ui.viewmodels

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.griffith.stepquest.data.UserInformation

class PwdViewModel : ViewModel() {

    var oldPasswordError by mutableStateOf("")
        private set

    var newPasswordError by mutableStateOf("")
        private set

    var confirmPasswordError by mutableStateOf("")
        private set

    var passwordChangeSuccess by mutableStateOf(false)
        private set

    fun resetErrors() {
        oldPasswordError = ""
        newPasswordError = ""
        confirmPasswordError = ""
    }

    fun changePassword(
        context: Context,
        oldPass: String,
        newPass: String,
        confirmPass: String
    ) {
        resetErrors()
        passwordChangeSuccess = false

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

        val info = UserInformation(context)
        val stored = info.getPassword()

        var storedPass = ""
        if (stored != null) {
            storedPass = stored
        }

        if (oldPass != storedPass) {
            oldPasswordError = "Old password is incorrect"
            return
        }

        info.savePassword(newPass)
        passwordChangeSuccess = true
    }
}
