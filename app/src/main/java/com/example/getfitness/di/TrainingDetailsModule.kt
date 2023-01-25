package com.example.getfitness.di

import com.example.getfitness.ui.details.TrainingDetailsViewModel
import com.example.getfitness.ui.details.viewpager.ExerciseAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val trainingDetailsModule = module {

    single {
        ExerciseAdapter()
    }

    viewModel {
        TrainingDetailsViewModel(get())
    }

}