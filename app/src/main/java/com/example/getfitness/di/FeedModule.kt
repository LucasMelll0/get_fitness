package com.example.getfitness.di

import com.example.getfitness.ui.feed.FeedViewModel
import com.example.getfitness.ui.feed.recyclerview.TrainingAdapter
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val feedModule = module {

    single {
        TrainingAdapter()
    }

    viewModel {
        FeedViewModel(get())
    }
}