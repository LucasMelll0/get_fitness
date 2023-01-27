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

    fun getTrainingByName(
        userId: String,
        name: Number,
        onSuccess: (training: Training, exercises: List<Exercise>) -> Unit,
        onError: () -> Unit = {}
    ) {
        firebaseHelper.getTrainingByName(userId, name, onSuccess, onError)
    }

    fun getTrainingByNameRealTime(
        userId: String,
        name: Number,
        onSuccess: (training: Training, exercises: List<Exercise>) -> Unit,
        onError: () -> Unit = {}
    ) {
        firebaseHelper.getTrainingByNameRealTime(userId, name, onSuccess, onError)
    }

    fun removeTraining(
        userId: String,
        trainingId: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        firebaseHelper.removeTraining(userId, trainingId, onSuccess, onError)
    }

    fun updateTraining(
        userId: String,
        training: Training,
        exercises: List<Exercise>,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        firebaseHelper.updateTraining(userId, training, exercises, onSuccess, onError)
    }

}