package com.example.getfitness.di

import com.example.getfitness.helper.FireStoreHelper
import com.example.getfitness.helper.StorageHelper
import com.example.getfitness.repository.TrainingRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.dsl.module

val trainingModule = module {

    single {
        FirebaseFirestore.getInstance()
    }

    factory {
        FirebaseStorage.getInstance().reference
    }

    single {
        StorageHelper(get())
    }

    single {
        FireStoreHelper(get(), get())
    }

    single {
        TrainingRepository(get())
    }
}