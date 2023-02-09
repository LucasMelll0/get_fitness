package com.example.getfitness.ui.details

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentTrainingDetailsBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.extensions.showSnackBar
import com.example.getfitness.model.Training
import com.example.getfitness.ui.details.bottomsheet.BottomSheetTrainingDescription
import com.example.getfitness.ui.details.viewpager.ExerciseAdapter
import com.example.getfitness.utils.checkConnection
import com.google.android.material.appbar.MaterialToolbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.math.abs


class TrainingDetailsFragment : Fragment() {

    private var _binding: FragmentTrainingDetailsBinding? = null
    private val binding get() = _binding!!
    private val viewModel: TrainingDetailsViewModel by viewModel()
    private val adapter: ExerciseAdapter by inject()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val args: TrainingDetailsFragmentArgs by navArgs()
    private var trainingId: Long? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTrainingDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkHasArgs()
        setsUpViewPager()
    }


    private fun checkHasArgs() {
        args.trainingId.also {
            if (it > 1) {
                trainingId = it
            } else {
                goToBack()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        context?.let {
            trainingId?.let {
                getTrainingByName(it)
            } ?: goToBack()
        }
    }

    private fun progressBar(visible: Boolean) {
        _binding?.let {
            val progressBar = binding.progressbarDetails
            progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    private fun getTrainingByName(name: Number) {
        currentUser?.let {
            val connected = checkConnection(requireContext())
            if (connected) {
                setsUpObservers()
                progressBar(true)
                viewModel.getTrainingByName(
                    it.uid,
                    name,
                    onSuccess = { date ->
                        setsUpToolbar(date)
                        progressBar(false)
                    },
                    onError = {
                            showSnackBar(getString(R.string.common_get_training_error))
                            goToBack()
                    }
                )
            } else {
                showSnackBar(getString(R.string.common_offline_message))
                goToBack()
            }

        } ?: goToBack()

    }

    private fun setsUpToolbar(date: String) {
        _binding?.let {
            val toolbar = binding.toolbarTrainingDetails
            toolbar.title = date
            setsUpNavigationButton(toolbar)
        }

    }

    private fun setsUpNavigationButton(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener {
            goToBack()
        }
    }

    private fun setsUpObservers() {
        viewModel.training.observe(this@TrainingDetailsFragment) {
            it?.let {
                setsUpButtonShowDescription(it)
            }
        }
        viewModel.exercises.observe(this@TrainingDetailsFragment) {
            adapter.submitList(it)
        }
    }

    private fun setsUpButtonShowDescription(training: Training) {
        _binding?.let {
            val buttonShowDescription = binding.buttonShowDescriptionTrainingDetails
            buttonShowDescription.setOnClickListener {
                setsUpBottomSheetDescription(training)
            }
        }
    }

    private fun setsUpBottomSheetDescription(training: Training) {
        val bottomSheet = BottomSheetTrainingDescription(training.description)
        bottomSheet.onClickEdit = {
            val action =
                TrainingDetailsFragmentDirections.actionTrainingDetailsFragmentToTrainingFormFragment(
                    training.name.toLong()
                )
            goTo(action)
        }
        bottomSheet.show(childFragmentManager, BottomSheetTrainingDescription.TAG)
    }


    private fun setsUpViewPager() {
        _binding?.let {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}