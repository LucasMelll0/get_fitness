package com.example.getfitness.repository

import com.example.getfitness.helper.AuthHelper
import com.example.getfitness.model.User

class AccountRepository(
    private val authHelper: AuthHelper
) {

    fun connect(
        user: User,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        authHelper.connect(user, onSuccess, onError)
    }


    fun register(
        user: User,
        onSuccess: () -> Unit = {},
        onError: (error: String) -> Unit = {},
    ) {
        authHelper.register(user, onSuccess, onError)

    }

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        authHelper.sendPasswordResetEmail(email, onSuccess, onError)
    }

    fun disconnect() {
        authHelper.disconnect()
    }
}