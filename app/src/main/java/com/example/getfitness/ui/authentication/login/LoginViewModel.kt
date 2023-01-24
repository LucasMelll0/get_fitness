package com.example.getfitness.ui.authentication.login

import androidx.lifecycle.ViewModel
import com.example.getfitness.model.User
import com.example.getfitness.repository.AccountRepository

class LoginViewModel(
    private val repository: AccountRepository
) : ViewModel() {

    fun connect(
        user: User,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {},
    ) {
        repository.connect(user, onSuccess, onError)
    }
}