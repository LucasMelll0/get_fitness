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

    private fun getExercises(): List<Exercise> = _exercises.value ?: emptyList()
}