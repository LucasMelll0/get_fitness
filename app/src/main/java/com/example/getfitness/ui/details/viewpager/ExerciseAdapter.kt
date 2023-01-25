package com.example.getfitness.ui.details.viewpager

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.getfitness.databinding.ItemExerciseTrainingDetailsBinding
import com.example.getfitness.extensions.tryToLoadImage
import com.example.getfitness.model.Exercise

class ExerciseAdapter : ListAdapter<Exercise, ExerciseAdapter.ExerciseViewHolder>(differCallBack) {

    companion object {
        private val differCallBack = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem == newItem
            }
        }
    }

    class ExerciseViewHolder(private val binding: ItemExerciseTrainingDetailsBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(exercise: Exercise) {
            val imageviewExercise = binding.imageviewExerciseItemTrainingDetails
            val editTextObservations = binding.textviewObservationsExerciseItemTrainingDetails

            exercise.apply {
                exercise.image?.let {
                    imageviewExercise.tryToLoadImage(it)
                }
                editTextObservations.text = observations
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseTrainingDetailsBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}