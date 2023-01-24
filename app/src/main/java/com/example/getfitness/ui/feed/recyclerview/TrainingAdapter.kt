package com.example.getfitness.ui.feed.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.getfitness.databinding.ItemTrainingBinding
import com.example.getfitness.model.Training
import com.example.getfitness.utils.formatTimestamp

class TrainingAdapter(
    var onClick: (id: String) -> Unit = {}
) : ListAdapter<Training, TrainingAdapter.TrainingViewHolder>(differCallback) {

    companion object {
        private val differCallback = object : DiffUtil.ItemCallback<Training>() {
            override fun areItemsTheSame(oldItem: Training, newItem: Training): Boolean {
                return oldItem.name == newItem.name
            }

            override fun areContentsTheSame(oldItem: Training, newItem: Training): Boolean {
                return oldItem == newItem
            }
        }
    }

    class TrainingViewHolder(private val binding: ItemTrainingBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bindItem(training: Training) {
                val data = binding.textviewDateTrainingItem
                val description = binding.textviewDescriptionTrainingItem
                training.apply {
                    data.text = formatTimestamp(this.date)
                    description.text = this.description
                }
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TrainingViewHolder {
        val binding =
            ItemTrainingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return TrainingViewHolder(binding)
    }

    override fun onBindViewHolder(holder: TrainingViewHolder, position: Int) {
        holder.bindItem(getItem(position))
    }
}