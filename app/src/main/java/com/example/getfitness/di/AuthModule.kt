package com.example.getfitness.di

import com.example.getfitness.repository.AccountRepository
import com.example.getfitness.ui.authentication.login.LoginViewModel
import com.example.getfitness.ui.authentication.register.RegisterViewModel
import com.example.getfitness.ui.authentication.resetpassword.ResetPasswordViewModel
import com.google.firebase.auth.FirebaseAuth
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val authModule = module {

    single {
        FirebaseAuth.getInstance()
    }

    single {
        AccountRepository(get(), get())
    }

    viewModel {
        RegisterViewModel(get())
    }

    viewModel {
        ResetPasswordViewModel(get())
    }

    viewModel {
        LoginViewModel(get())
    }
}