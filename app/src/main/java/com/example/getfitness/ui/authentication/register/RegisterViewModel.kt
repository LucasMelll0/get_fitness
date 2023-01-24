package com.example.getfitness.ui.authentication.register

import androidx.lifecycle.ViewModel
import com.example.getfitness.model.User
import com.example.getfitness.repository.AccountRepository

class RegisterViewModel(
    private val repository: AccountRepository
) : ViewModel() {

    fun register(
        user: User,
        onSuccess: () -> Unit = {},
        onError: (error: String) -> Unit = {},
    ) {
        repository.register(user, onSuccess, onError)
    }
}