package com.example.getfitness.ui.details

import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentTrainingDetailsBinding
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.model.Training
import com.example.getfitness.ui.details.viewpager.ExerciseAdapter
import com.example.getfitness.utils.formatTimestamp
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class TrainingDetailsFragment : Fragment() {

    private var _binding: FragmentTrainingDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrainingDetailsViewModel by viewModel()
    private val adapter: ExerciseAdapter by inject()
    private val currentUser: FirebaseUser? by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpViewPager()
    }

    override fun onResume() {
        super.onResume()
        getTrainingByName(1674593257447)
    }

    private fun getTrainingByName(name: Number) {
        Log.i("Testes", "getTrainingByName: ")
        currentUser?.let {
            setsUpObservers()
            viewModel.getTrainingByName(
                it.uid,
                name,
                onSuccess = {
                    Log.i("Testes", "getTrainingByName: success")
                },
                onError = {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.common_get_training_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                    goToBack()
                }
            )
        } ?: goToBack()

    }

    private fun setsUpObservers() {
        viewModel.training.observe(this@TrainingDetailsFragment) {
            it?.let {
                updateUI(it)
            }
        }
        viewModel.exercises.observe(this@TrainingDetailsFragment) {
            adapter.submitList(it)
        }
    }

    private fun updateUI(training: Training) {
        val textviewDescription = binding.textviewDescriptionTrainingDetails
        val textviewDate = binding.textviewDateTrainingItem

        training.apply {
            textviewDescription.text = description
            textviewDate.text = formatTimestamp(date)
        }
    }

    private fun setsUpViewPager() {
        val viewPager = binding.viewpagerTrainingDetails
        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(
            MarginPageTransformer(
                (40 * Resources.getSystem()
                    .displayMetrics.density).toInt()
            )
        )
        compositePageTransformer.addTransformer { page, position ->
            val r = 1 - abs(position)
            page.scaleY = (0.80f + r * 0.20f)
        }
        viewPager.also {
            it.clipChildren = false
            it.clipToPadding = false
            it.offscreenPageLimit = 3
            (it.getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            it.setPageTransformer(compositePageTransformer)
            it.adapter = this.adapter
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}