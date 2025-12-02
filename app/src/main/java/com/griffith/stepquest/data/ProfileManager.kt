package com.griffith.stepquest.data

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

object ProfileManager {

    private val auth    = FirebaseAuth.getInstance()
    private val db      = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    fun uploadProfileImage(imageUri: Uri, onSuccess: (String) -> Unit, onError: (String) -> Unit) {

        val currentUser = auth.currentUser
        if (currentUser == null) {
            onError("Not logged in")
            return
        }

        val uid = currentUser.uid
        val ref = storage.reference.child("profile_images").child(uid + ".jpg")

        ref.putFile(imageUri)
            .addOnSuccessListener {
                ref.downloadUrl
                    .addOnSuccessListener { url ->
                        db.collection("users").document(uid)
                            .update("profile_picture", url.toString())
                            .addOnSuccessListener { onSuccess(url.toString()) }
                            .addOnFailureListener { onError("Could not save URL") }
                    }
                    .addOnFailureListener { onError("Could not get URL") }
            }
            .addOnFailureListener { onError("Upload failed") }
    }
}
