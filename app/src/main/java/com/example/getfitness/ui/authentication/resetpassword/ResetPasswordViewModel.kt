package com.example.getfitness.ui.authentication.resetpassword

import androidx.lifecycle.ViewModel
import com.example.getfitness.repository.AccountRepository

class ResetPasswordViewModel(
    private val repository: AccountRepository
): ViewModel() {

    fun sendPasswordResetEmail(
        email: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
        onOffline: () -> Unit = {}
    ) {
        repository.sendPasswordResetEmail(email, onSuccess, onError, onOffline)
    }

}