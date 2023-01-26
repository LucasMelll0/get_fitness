package com.example.getfitness.ui.form

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.getfitness.model.Exercise
import com.example.getfitness.model.Training
import com.example.getfitness.repository.TrainingRepository
import com.google.firebase.Timestamp

class TrainingFormViewModel(
    private val repository: TrainingRepository
) : ViewModel() {

    private var _exercises = MutableLiveData<List<Exercise>>(emptyList())
    internal val exercises: LiveData<List<Exercise>> = _exercises

    private var _date = MutableLiveData(Timestamp.now())
    internal val date: LiveData<Timestamp> = _date

    fun saveTraining(training: Training, onSuccess: () -> Unit = {}, onError: () -> Unit = {}) {
        repository.saveTraining(training, getExercises(), onSuccess, onError)
    }

    fun setDate(date: Timestamp) {
        _date.postValue(date)
    }

    fun getDate(): Timestamp = _date.value ?: Timestamp.now()


    fun addExercise(exercise: Exercise) {
        val newList = _exercises.value?.map { it.copy() }?.toMutableList() ?: mutableListOf()
        newList.add(exercise)
        _exercises.postValue(newList)
    }

    fun removeExercise(exercise: Exercise) {
        val newList = _exercises.value?.map { it.copy() }?.toMutableList() ?: mutableListOf()
        newList.remove(exercise)
        _exercises.postValue(newList)
    }

    private fun getExercises(): List<Exercise> = _exercises.value ?: emptyList()

    fun getByTrainingName(
        userId: String,
        name: Number,
        onSuccess: (description: String) -> Unit = {},
        onError: () -> Unit = {}
    ) {
        repository.getTrainingByName(
            userId,
            name,
            onSuccess = { training, exercises ->
                _exercises.postValue(exercises)
                setDate(training.date)
                onSuccess(training.description)
            },
            onError = onError
        )
    }

    fun removeTraining(
        userId: String,
        trainingId: String,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        repository.removeTraining(userId, trainingId, onSuccess, onError)
    }

    fun updateTraining(
        userId: String,
        training: Training,
        onSuccess: () -> Unit = {},
        onError: () -> Unit = {}
    ) {
        repository.updateTraining(userId, training, getExercises(), onSuccess, onError)
    }
}