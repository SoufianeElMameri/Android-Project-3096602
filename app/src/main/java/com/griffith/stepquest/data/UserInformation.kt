package com.griffith.stepquest.data

import android.content.Context
import androidx.core.content.edit

// this class will store the user info inside the mobile storage and read it
class UserInformation(context: Context) {

    // storage file
    private val prefs = context.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

    // save the user login state
    fun saveLoginState(isLoggedIn: Boolean) {
        prefs.edit {
            putBoolean("logged_in", isLoggedIn)
        }
    }

    // read the user login state
    fun isUserLoggedIn(): Boolean {
        return prefs.getBoolean("logged_in", false)
    }

    // save the user name
    fun saveUsername(username: String) {
        prefs.edit {
            putString("user_name", username)
        }
    }

    // read the user name
    fun getUsername(): String? {
        return prefs.getString("user_name", null)
    }

    // save the user email
    fun saveEmail(email: String) {
        prefs.edit {
            putString("user_email", email)
        }
    }

    // read the user email
    fun getEmail(): String? {
        return prefs.getString("user_email", null)
    }

    // save the user password
    fun savePassword(password: String) {
        prefs.edit {
            putString("user_password", password)
        }
    }

    // read the user password
    fun getPassword(): String? {
        return prefs.getString("user_password", null)
    }


    // clear all user data
    fun clearUserData() {
        prefs.edit {
            clear()
        }
    }
}
