package com.example.getfitness.ui.feed

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.getfitness.model.Training
import com.example.getfitness.repository.AccountRepository
import com.example.getfitness.repository.TrainingRepository
import kotlinx.coroutines.launch

class FeedViewModel(
    private val trainingRepository: TrainingRepository,
    private val accountRepository: AccountRepository
) : ViewModel() {

    private var _trainings = MutableLiveData<List<Training>>(emptyList())
    internal val trainings: LiveData<List<Training>> = _trainings

    fun getAllTrainings(userId: String, onError: () -> Unit = {}) {
        trainingRepository.getAllTrainings(
            userId,
            onSuccess = {
                viewModelScope.launch { _trainings.postValue(it) }
            },
            onError
        )
    }

    fun disconnect() {
        accountRepository.disconnect()
    }

}