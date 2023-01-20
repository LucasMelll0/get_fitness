package com.example.getfitness.repository

import android.content.Context
import com.example.getfitness.model.User
import com.example.getfitness.utils.checkConnection
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest

class AccountRepository(
    private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {

    fun connect(
        user: User,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onOffline: () -> Unit = {}
    ) {
        val connected = checkConnection(context)
        if (connected) {
            firebaseAuth.signInWithEmailAndPassword(
                user.email,
                user.password
            ).addOnSuccessListener {
                onSuccess()
            }.addOnFailureListener {
                    onError()
                }
        } else {
            onOffline()
        }
    }


    fun register(
        user: User,
        onSuccess: () -> Unit = {},
        onError: (error: String) -> Unit = {},
        onOffline: () -> Unit = {}
    ) {
        val connected = checkConnection(context)
        if (connected) {
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
        } else {
            onOffline()
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
        onOffline: () -> Unit = {}
    ) {
        val connected = checkConnection(context)
        if (connected) {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .addOnSuccessListener {
                    onSuccess()
                }
                .addOnFailureListener {
                    onError()
                }
        } else {
            onOffline()
        }
    }
}