package com.example.getfitness.ui.feed

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentFeedBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.ui.feed.recyclerview.TrainingAdapter
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseUser
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModel()
    private val adapter: TrainingAdapter by inject()
    private val currentUser: FirebaseUser? by inject()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpRecyclerView()
        setsUpButtonAdd()
    }

    private fun setsUpPopBackStack() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                it.finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setsUpPopBackStack()
    }

    private fun setsUpButtonAdd() {
        val buttonAdd = binding.buttonAddTraining
        buttonAdd.setOnClickListener {
            goTo(R.id.action_feedFragment_to_trainingFormFragment)
        }
    }

    private fun setsUpRecyclerView() {
        val recyclerView = binding.recyclerviewFeed
        recyclerView.also {
            it.adapter = this.adapter
            it.layoutManager = LinearLayoutManager(requireContext())
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tryGetAllTrainings()

    }

    private fun tryGetAllTrainings() {
        currentUser?.let {
            viewModel.getAllTrainings(
                it.uid,
                onError = {
                    Snackbar.make(
                        requireView(),
                        getString(R.string.fragment_feed_get_trainings_error),
                        Snackbar.LENGTH_LONG
                    ).show()
                    updateRecyclerview()
                }
            )

        } ?: goToBack()

    }

    private fun updateRecyclerview() {
        viewModel.trainings.observe(this@FeedFragment) {
            if (it.isNotEmpty()) {
                adapter.submitList(it)
            } else {
                Log.i("Teste", "updateRecyclerview: lista vazia")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}