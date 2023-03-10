package com.example.getfitness.ui.form

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentTrainingFormBinding
import com.example.getfitness.extensions.*
import com.example.getfitness.model.Training
import com.example.getfitness.ui.form.exerciseform.ExerciseFormDialogFragment
import com.example.getfitness.ui.form.recyclerview.ExerciseFormAdapter
import com.example.getfitness.utils.formatMillisToTimestamp
import com.example.getfitness.utils.formatTimestamp
import com.example.getfitness.utils.getDatePicker
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel


class TrainingFormFragment : Fragment() {

    private var _binding: FragmentTrainingFormBinding? = null
    private val binding get() = _binding
    private val adapter: ExerciseFormAdapter by inject()
    private val viewModel: TrainingFormViewModel by viewModel()
    private val args: TrainingFormFragmentArgs by navArgs()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private var trainingName: Number? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentTrainingFormBinding.inflate(inflater, container, false)
        return binding?.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpToolbar()
        setsUpButtonAddExercise()
    }

    override fun onStart() {
        super.onStart()
        checkArgs()
    }

    private fun checkArgs() {
        args.trainingName.also {
            if (it > 1) {
                trainingName = it
                getTraining()
            }
        }
    }

    private fun progressBar(visible: Boolean) {
        binding?.let {
            val progressBar = binding!!.progressbarForm
            progressBar.visibility = if (visible) View.VISIBLE else View.GONE
        }
    }

    private fun getTraining() {
        currentUser?.let { user ->
            safeRun(onOffline = { goToBack() }) {
                progressBar(true)
                viewModel.getByTrainingName(
                    user.uid,
                    trainingName!!,
                    onSuccess = {
                        fillDescription(it)
                        progressBar(false)
                    },
                    onError = {
                        showSnackBar(getString(R.string.common_get_training_error))
                        goTo(R.id.action_trainingFormFragment_to_feedFragment)
                    }
                )
            }
        }
    }

    private fun fillDescription(description: String) {
        binding?.let {
            val editTextObservations = binding!!.edittextDescriptionTrainingForm.editText
            editTextObservations?.setText(description)
        }

    }

    private fun setsUpDateInput() {
        binding?.let {
            val buttonSetDate = binding!!.buttonSetDateTrainingForm
            val editTextDate = binding!!.edittextDateTrainingForm.editText
            editTextDate?.let { editText ->
                val datePicker = setsUpDatePicker(editText)
                val datePickerTag = "DatePicker Training"
                buttonSetDate.setOnClickListener {
                    datePicker.show(childFragmentManager, datePickerTag)
                }
                viewModel.date.observe(this@TrainingFormFragment) {
                    val formattedTimestamp = formatTimestamp(it)
                    editText.setText(formattedTimestamp)
                }
            }
        }
    }

    private fun setsUpDatePicker(editTextDate: EditText): MaterialDatePicker<Long> {
        val datePicker = getDatePicker(getString(R.string.datepicker_title))
        datePicker.addOnPositiveButtonClickListener { dateInMilli ->
            val timestamp = formatMillisToTimestamp(dateInMilli)
            val formattedTimestamp = formatTimestamp(timestamp)
            viewModel.setDate(timestamp)
            editTextDate.setText(formattedTimestamp)
        }
        return datePicker
    }

    override fun onResume() {
        super.onResume()
        setsUpDateInput()
        setsUpRecyclerView()
        setsUpConfirmButton()
    }

    private fun setsUpConfirmButton() {
        binding?.let{
            val confirmButton = binding!!.buttonConfirmTrainingForm
            confirmButton.setOnClickListener {
                saveTraining()
            }
        }
    }

    private fun setsUpAdapterRemoveFunction() {
        adapter.onClickRemove = {
            viewModel.removeExercise(it)
        }
    }

    private fun setsUpButtonAddExercise() {
        binding.let {
            val buttonAddExercise = binding!!.buttonAddExercise
            buttonAddExercise.setOnClickListener {
                val exerciseFormDialogFragment = ExerciseFormDialogFragment()
                exerciseFormDialogFragment.onConfirmation = {
                    viewModel.addExercise(it)
                }
                exerciseFormDialogFragment.show(
                    childFragmentManager, ExerciseFormDialogFragment.TAG
                )
            }
        }
    }

    private fun setsUpToolbar() {
        binding?.let {
            val toolbar = binding!!.toolbarFormTraining
            setsUpToolbarNavigationButton(toolbar)
            setsUpToolbarMenu(toolbar)
        }
    }

    private fun setsUpToolbarNavigationButton(toolbar: MaterialToolbar) {
        toolbar.setNavigationOnClickListener {
            goToBack()
        }
    }

    private fun setsUpToolbarMenu(toolbar: MaterialToolbar) {
        toolbar.inflateMenu(R.menu.menu_training_form)
        toolbar.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.menu_item_delete_training_form -> {
                    showDeleteAlertDialog()
                    true
                }
                else -> false
            }
        }
    }

    private fun showDeleteAlertDialog() {
        val message = getString(R.string.fragment_training_form_alert_dialog_message)
        showAlertDialog(message, onConfirm = { removeTraining() })
    }

    private fun removeTraining() {
        trainingName?.let {
            currentUser?.let {
                safeRun(onOffline = { progressBar(false) }) {
                    progressBar(true)
                    viewModel.removeTraining(
                        it.uid,
                        trainingName.toString(),
                        onSuccess = {
                            showSnackBar(getString(R.string.fragment_training_form_successfully_delete_training_message))
                            goTo(R.id.action_trainingFormFragment_to_feedFragment)
                        },
                        onError = {
                            progressBar(false)
                            showSnackBar(getString(R.string.fragment_training_form_delete_training_error))
                        }
                    )
                }
            }
        } ?: goToBack()
    }

    private fun saveTraining() {
        if (noEmptyFields()) {
            safeRun(onOffline = { progressBar(false) }) {
                val training = createTraining()
                training?.let {
                    progressBar(true)
                    trainingName?.let {
                        viewModel.updateTraining(
                            currentUser!!.uid,
                            training,
                            onSuccess = {
                                showSnackBar(getString(R.string.fragment_training_form_training_save_success))
                                goToBack()
                            },
                            onError = {
                                progressBar(false)
                                showSnackBar(getString(R.string.fragment_training_form_save_error))
                            }
                        )
                    } ?: run {
                        viewModel.saveTraining(
                            training,
                            onSuccess = {
                               showSnackBar(getString(R.string.fragment_training_form_training_save_success))
                                goToBack()
                            },
                            onError = {
                                progressBar(false)
                                showSnackBar(getString(R.string.fragment_training_form_save_error))
                            }
                        )
                    }
                }
            }
        }
    }

    private fun createTraining(): Training? {
        return FirebaseAuth.getInstance().currentUser?.let { user ->
            binding?.let {
                val editTextDescription = binding!!.edittextDescriptionTrainingForm.editText
                trainingName?.let {
                    editTextDescription?.let {
                        val description = it.text.toString()
                        val date = viewModel.getDate()
                        val author = user.uid
                        Training(
                            name = trainingName!!,
                            description = description,
                            date = date,
                            author = author
                        )
                    }
                } ?: run {
                    editTextDescription?.let {
                        val name =
                            Timestamp.now().seconds * 1000 + Timestamp.now().nanoseconds / 1000000
                        val description = it.text.toString()
                        val date = viewModel.getDate()
                        val author = user.uid
                        Training(
                            name = name,
                            description = description,
                            date = date,
                            author = author
                        )
                    }
                }
            }
        }

    }


    private fun noEmptyFields(): Boolean {
        return binding?.let {
            val editTextDescription = binding!!.edittextDescriptionTrainingForm
            editTextDescription.editText?.let {
                if (it.text.isEmpty()) {
                    editTextDescription.error = getString(R.string.common_field_required)
                    false
                } else {
                    true
                }
            } ?: false
        } ?: false
    }

    private fun setsUpRecyclerView() {
        binding?.let {
            val recyclerView = binding!!.recyclerviewExercisesTrainingForm
            recyclerView.also {
                it.adapter = adapter
                it.layoutManager = LinearLayoutManager(requireContext()).apply {
                    orientation = LinearLayoutManager.HORIZONTAL
                }
            }
            viewModel.exercises.observe(this@TrainingFormFragment) {
                adapter.submitList(it)
            }
            setsUpAdapterRemoveFunction()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}