package com.example.getfitness.repository

import com.example.getfitness.helper.FireStoreHelper
import com.example.getfitness.model.Exercise
import com.example.getfitness.model.Training

class TrainingRepository(
    private val firebaseHelper: FireStoreHelper
) {


    fun getAllTrainings(
        userId: String,
        onSuccess: (List<Training>) -> Unit = {},
        onError: () -> Unit = {}
    ) {
        firebaseHelper.getAllTrainings(userId, onSuccess, onError)
    }

    fun saveTraining(
        training: Training,
        exercises: List<Exercise>,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        firebaseHelper.saveTraining(training, exercises, onSuccess, onError)
    }

}