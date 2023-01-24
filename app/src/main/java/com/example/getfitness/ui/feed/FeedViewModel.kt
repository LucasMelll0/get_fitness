package com.example.getfitness.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.getfitness.model.Training
import com.example.getfitness.repository.TrainingRepository

class FeedViewModel(
    private val repository: TrainingRepository
) : ViewModel() {

    private var _trainings = MutableLiveData<List<Training>>(emptyList())
    internal val trainings: LiveData<List<Training>> = _trainings

    fun getAllTrainings(userId: String, onError: () -> Unit = {}){
        repository.getAllTrainings(
            userId,
            onSuccess = { _trainings.postValue(it) },
            onError
        )
    }
}