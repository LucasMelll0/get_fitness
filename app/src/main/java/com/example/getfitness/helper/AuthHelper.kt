package com.example.getfitness.helper

import com.example.getfitness.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class AuthHelper(
    private val firebaseAuth: FirebaseAuth
) {

    fun connect(
        user: User,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        firebaseAuth.signInWithEmailAndPassword(
            user.email,
            user.password
        ).addOnSuccessListener {
            onSuccess()
        }.addOnFailureListener {
            onError()
        }
    }


    fun register(
        user: User,
        onSuccess: () -> Unit = {},
        onError: (error: String) -> Unit = {},
    ) {
        firebaseAuth.createUserWithEmailAndPassword(
            user.email,
            user.password
        ).addOnSuccessListener {
            changeUsername(user.name)
            onSuccess()
        }.addOnFailureListener {
            val error = it.toString()
            onError(error)
        }

    }

    private fun changeUsername(name: String) {
        firebaseAuth.currentUser?.let {
            val nameChangeRequest = userProfileChangeRequest {
                displayName = name
            }
            it.updateProfile(nameChangeRequest)
        }
    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        firebaseAuth
            .sendPasswordResetEmail(email)
            .addOnSuccessListener {
                onSuccess()
            }
            .addOnFailureListener {
                onError()
            }
    }

}