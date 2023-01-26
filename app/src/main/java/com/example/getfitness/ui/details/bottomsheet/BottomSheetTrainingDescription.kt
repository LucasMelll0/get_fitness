package com.example.getfitness.ui.details.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.getfitness.databinding.BottomSheetTrainingDescriptionBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class BottomSheetTrainingDescription(
    private val description: String,
    var onClickEdit: () -> Unit = {}
) : BottomSheetDialogFragment() {

    companion object {
        val TAG = "BottomSheet Training Description Tag"
    }

    private var _binding: BottomSheetTrainingDescriptionBinding? = null
    private val binding get() = _binding!!


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetTrainingDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fillDescription()
        setsUpButtonEdit()
    }

    private fun setsUpButtonEdit() {
        val buttonEdit = binding.buttonEditDescriptionBottomSheet
        buttonEdit.setOnClickListener {
            onClickEdit()
        }
    }

    private fun fillDescription() {
        val textviewDescription = binding.textviewDescriptionBottomSheet
        textviewDescription.text = description
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}