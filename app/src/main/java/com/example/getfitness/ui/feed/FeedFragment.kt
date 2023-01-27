package com.example.getfitness.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentFeedBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.ui.feed.recyclerview.TrainingAdapter
import com.example.getfitness.utils.checkConnection
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class FeedFragment : Fragment() {

    private var _binding: FragmentFeedBinding? = null
    private val binding get() = _binding!!
    private val viewModel: FeedViewModel by viewModel()
    private val adapter: TrainingAdapter by inject()
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFeedBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        checkCurrentUser()
        setsUpToolbar()
        setsUpRecyclerView()
        setsUpButtonAdd()
    }

    private fun setsUpToolbar() {
        val toolbar = binding.toolbarFeed
        setsUpNavigationOnclick(toolbar)
    }

    private fun setsUpNavigationOnclick(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener {
            currentUser?.let {
                viewModel.disconnect()
                goTo(R.id.action_feedFragment_to_loginFragment)
            }
        }
    }

    private fun checkCurrentUser() {
        currentUser ?: goTo(R.id.action_feedFragment_to_loginFragment)
    }

    private fun setsUpPopBackStack() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                it.finish()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        setsUpTrainingsObserver()
        tryGetAllTrainings()
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
            it.layoutManager = StaggeredGridLayoutManager(
                2,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
        setsUpAdapterOnClick()
    }

    private fun setsUpAdapterOnClick() {
        adapter.onClick = {
            val action =
                FeedFragmentDirections.actionFeedFragmentToTrainingDetailsFragment(it)
            goTo(action)
        }
    }

    private fun tryGetAllTrainings() {
        currentUser?.let {
            val connected = checkConnection(requireContext())
            if (connected) {
                viewModel.getAllTrainings(
                    it.uid,
                    onError = {
                        view?.let { view ->
                            Snackbar.make(
                                view,
                                getString(R.string.fragment_feed_get_trainings_error),
                                Snackbar.LENGTH_LONG
                            ).show()
                        }
                    }
                )
            } else {
                Snackbar.make(
                    requireView(),
                    getString(R.string.common_offline_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }

        } ?: goToBack()

    }

    private fun setsUpTrainingsObserver() {
        viewModel.trainings.observe(this@FeedFragment) {
            adapter.submitList(it)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}