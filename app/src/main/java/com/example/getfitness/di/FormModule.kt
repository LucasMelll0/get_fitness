package com.example.getfitness.di

import com.example.getfitness.ui.form.TrainingFormViewModel
import com.example.getfitness.ui.form.recyclerview.ExerciseFormAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val formModule = module {
    single {
        ExerciseFormAdapter()
    }

    viewModel {
        TrainingFormViewModel(get())
    }
}