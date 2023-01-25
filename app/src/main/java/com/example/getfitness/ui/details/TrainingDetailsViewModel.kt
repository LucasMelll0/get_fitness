package com.example.getfitness.ui.details

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.getfitness.model.Exercise
import com.example.getfitness.model.Training
import com.example.getfitness.repository.TrainingRepository

class TrainingDetailsViewModel(
    private val repository: TrainingRepository
) : ViewModel() {

    private var _training = MutableLiveData<Training?>(null)
    internal val training: LiveData<Training?> = _training

    private var _exercises = MutableLiveData<List<Exercise>>(emptyList())
    internal val exercises: LiveData<List<Exercise>> = _exercises


    fun getTrainingByName(
        userId: String,
        name: Number,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        repository.getTrainingByName(
            userId,
            name,
            onSuccess = {training, exercises ->
                _training.postValue(training)
                _exercises.postValue(exercises)
                onSuccess()
            },
            onError = onError
        )

    }

}