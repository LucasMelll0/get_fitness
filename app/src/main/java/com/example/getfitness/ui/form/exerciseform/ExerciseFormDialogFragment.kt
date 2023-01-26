package com.example.getfitness.ui.form.exerciseform

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.DialogFragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentDialogExerciseFormBinding
import com.example.getfitness.extensions.tryToLoadImage
import com.example.getfitness.model.Exercise
import com.google.firebase.auth.FirebaseUser
import org.koin.android.ext.android.inject
import kotlin.random.Random

class ExerciseFormDialogFragment(
    var onConfirmation: (exercise: Exercise) -> Unit = {}
) : DialogFragment() {

    companion object {
        const val TAG = "ExerciseFormDialogFragmentTag"
    }

    private var _binding: FragmentDialogExerciseFormBinding? = null
    private val binding get() = _binding!!
    private val currentUser: FirebaseUser? by inject()
    private var imageUri: Uri? = null
    private val pickImage = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            imageUri = it
            binding.imageviewExerciseFormDialogFragment.tryToLoadImage(uri)
        }
    }
    private val requestPermission = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            pickImage.launch("image/*")
        } else {
            Toast.makeText(
                requireContext(),
                getString(R.string.common_permission_denied),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDialogExerciseFormBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpImageview()
        setsUpCancelButton()
        setsUpConfirmationButton()
    }

    override fun onStart() {
        super.onStart()
        dialog?.let {
            it.setCanceledOnTouchOutside(false)
            it.window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
            )
            it.window?.setBackgroundDrawable(
                ResourcesCompat.getDrawable(
                    resources,
                    R.drawable.transparent_drawable,
                    requireActivity().theme
                )
            )
        }
    }

    private fun setsUpCancelButton() {
        val cancelButton = binding.buttonCancelExerciseForm
        cancelButton.setOnClickListener {
            dialog?.dismiss()
        }
    }

    private fun setsUpConfirmationButton() {
        val confirmationButton = binding.buttonConfirmExerciseForm
        confirmationButton.setOnClickListener {
            if (noEmptyFields()) {
                val exercise = createExercise()
                exercise?.let {
                    onConfirmation(exercise)
                }
                dialog?.dismiss()
            }
        }
    }

    private fun createExercise(): Exercise? {
        return currentUser?.let {
            val observations =
                binding.edittextObservationsExerciseForm.editText!!.text.toString().trim()
            val maxNameValue: Long = 100000
            Exercise(
                name = Random.nextLong(0, maxNameValue),
                observations = observations,
                image = imageUri,
                author = it.uid
            )
        }
    }

    private fun noEmptyFields(): Boolean {
        val editTextObservations = binding.edittextObservationsExerciseForm

        return if (editTextObservations.editText!!.text.isEmpty()) {
            editTextObservations.error = getString(R.string.common_field_required)
            false
        } else {
            editTextObservations.error = null
            true
        }
    }

    private fun setsUpImageview() {
        val imageViewExercise = binding.imageviewExerciseFormDialogFragment
        imageViewExercise.setOnClickListener {
            pickImageFromGallery()
        }
    }


    private fun pickImageFromGallery() {
        val permission = READ_EXTERNAL_STORAGE
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission.launch(permission)
        } else {
            pickImage.launch("image/*")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }

}