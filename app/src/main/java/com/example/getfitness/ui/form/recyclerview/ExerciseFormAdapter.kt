package com.example.getfitness.ui.form.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.getfitness.databinding.ItemExerciseTrainingFormBinding
import com.example.getfitness.model.Exercise

class ExerciseFormAdapter(
    var onClickRemove: (exercise: Exercise) -> Unit = {}
) :
    ListAdapter<Exercise, ExerciseFormAdapter.ExerciseFormViewHolder>(differCalBack) {

    companion object {
        private val differCalBack = object : DiffUtil.ItemCallback<Exercise>() {
            override fun areItemsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Exercise, newItem: Exercise): Boolean {
                return oldItem == newItem
            }

        }
    }

    inner class ExerciseFormViewHolder(private val binding: ItemExerciseTrainingFormBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bindItem(exercise: Exercise) {
            val imageviewExercise = binding.imageviewItemTrainingForm
            val textviewObservations = binding.textviewObservationsExerciseTrainingForm
            val buttonRemove = binding.buttonRemoveExerciseTrainingForm
            exercise.apply {
                imageviewExercise.load(this.image)
                textviewObservations.text = this.observations
            }
            buttonRemove.setOnClickListener {
                onClickRemove(exercise)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseFormViewHolder {
        val binding = ItemExerciseTrainingFormBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ExerciseFormViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseFormViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}