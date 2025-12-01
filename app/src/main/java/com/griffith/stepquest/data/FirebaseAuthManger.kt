package com.griffith.stepquest.data

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseAuthManger{

    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()

//***************************************************** REGISTER USER *****************************************************

    fun registerUser(
        username: String,
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->

                val uid = result.user!!.uid

                // FIREBase user account
                val userProfile = hashMapOf(
                    "username" to username,
                    "email" to email,
                    "profile_picture" to "",
                    "rank_id" to "bronze",
                    "level" to 1
                )

                db.collection("users").document(uid)
                    .set(userProfile)
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { e ->
                        onError(e.message ?: "Failed to create user profile")
                    }
            }
            .addOnFailureListener { e ->
                onError(e.message ?: "Registration failed")
            }
    }
//***************************************************** LOGIN  *****************************************************
    fun loginUser(
        email: String,
        password: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e ->
                onError(e.message ?: "Login failed")
            }
    }

//***************************************************** LOGOUT  *****************************************************
    fun logoutUser() {
        auth.signOut()
    }

//***************************************************** LOGIN STATE *****************************************************
    fun isLoggedIn(): Boolean {
        return auth.currentUser != null
    }

//***************************************************** GET CURRENT USER ID *****************************************************
    fun getUid(): String? {
        return auth.currentUser?.uid
    }
}
