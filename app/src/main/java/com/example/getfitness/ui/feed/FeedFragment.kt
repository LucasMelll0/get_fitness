package com.example.getfitness.ui.feed

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentFeedBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.extensions.showAlertDialog
import com.example.getfitness.extensions.showSnackBar
import com.example.getfitness.ui.feed.recyclerview.TrainingAdapter
import com.example.getfitness.utils.checkConnection
import com.google.android.material.appbar.MaterialToolbar
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
    }

    private fun checkCurrentUser() {
        currentUser?.let {
            setsUpRecyclerView()
            setsUpButtonAdd()
        } ?: goTo(R.id.action_feedFragment_to_loginFragment)
    }

    private fun setsUpToolbar() {
        val toolbar = binding.toolbarFeed
        toolbar.inflateMenu(R.menu.menu_feed)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_search_feed -> {
                    val search = toolbar.menu.findItem(R.id.menu_item_search_feed)
                    val searchView = search?.actionView as? SearchView
                    setsUpSearchView(searchView)
                    true
                }
                else -> false
            }
        }
        setsUpNavigationOnclick(toolbar)
    }

    private fun setsUpSearchView(searchView: SearchView?) {
        searchView?.let {
            it.isSubmitButtonEnabled = false
            it.setOnCloseListener {
                setsUpTrainingsObserver()
                false
            }
            it.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    newText?.let {
                        if (newText.isNotEmpty()) {
                            val filteredList = viewModel.search(newText)
                            adapter.submitList(filteredList)
                        } else {
                            setsUpTrainingsObserver()
                        }
                    } ?: setsUpTrainingsObserver()
                    return false
                }

            })
        }
    }

    private fun setsUpNavigationOnclick(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener {
            val message = "Do you really want to disconnect?"
            showAlertDialog(message, onConfirm = { disconnect() })
        }
    }

    private fun disconnect() {
        currentUser?.let {
            viewModel.removeAllTrainingsObserver()
            viewModel.disconnect()
            goTo(R.id.action_feedFragment_to_loginFragment)
        }
    }

    private fun setsUpPopBackStack() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                it.finish()
            }
        }
    }

    private fun setsUpRecyclerView() {
        val recyclerView = binding.recyclerviewFeed
        val columnsQuantity = 2
        recyclerView.also {
            it.adapter = this.adapter
            it.layoutManager = StaggeredGridLayoutManager(
                columnsQuantity,
                StaggeredGridLayoutManager.VERTICAL
            )
        }
        setsUpAdapterOnClick()
    }

    private fun progressBar(visible: Boolean) {
        _binding?.let {
            val progressBar = binding.progressbarFeed
            progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        }

    }

    override fun onStart() {
        super.onStart()
        setsUpToolbar()
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
                progressBar(true)
                viewModel.getAllTrainings(
                    it.uid,
                    onSuccess = {
                        progressBar(false)
                    },
                    onError = {
                        progressBar(false)
                        showSnackBar(getString(R.string.fragment_feed_get_trainings_error))
                    }
                )
            } else {
                showSnackBar(getString(R.string.common_offline_message))
            }

        } ?: goToBack()

    }

    private fun emptyListMessage(visible: Boolean) {
        val viewEmptyList = binding.viewEmptyListMessage
        viewEmptyList.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun setsUpTrainingsObserver() {
        viewModel.trainings.observe(this@FeedFragment) {
            adapter.submitList(it)
            if (it.isEmpty()) {
                emptyListMessage(true)
            } else {
                emptyListMessage(false)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}