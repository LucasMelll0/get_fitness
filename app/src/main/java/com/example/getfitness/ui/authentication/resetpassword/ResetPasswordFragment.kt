package com.example.getfitness.ui.authentication.resetpassword

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentResetPasswordBinding
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.extensions.safeRun
import com.example.getfitness.extensions.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel


class ResetPasswordFragment : Fragment() {

    private var _binding: FragmentResetPasswordBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ResetPasswordViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResetPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpToolbar()
        setsUpSubmitButton()
    }

    private fun setsUpToolbar() {
        val toolbar = binding.toolbarResetPassword
        toolbar.setNavigationOnClickListener {
            goToBack()
        }
    }

    private fun setsUpSubmitButton() {
        val submitButton = binding.buttonSubmitResetPassword
        submitButton.setOnClickListener {
            if (noEmptyField()) {
                progressBar(true)
                sendPasswordResetEmail()
            }
        }
    }

    private fun progressBar(visible: Boolean) {
        val progressBar = binding.progressbarResetPassword
        progressBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun sendPasswordResetEmail() {
        val email = binding.edittextEmailResetPassword.editText!!.text.toString().trim()
        safeRun(onOffline = { progressBar(false) }) {
            viewModel.sendPasswordResetEmail(
                email,
                onSuccess = {
                    progressBar(false)
                    showSnackBar(getString(R.string.fragment_reset_password_success_message))
                },
                onError = {
                    progressBar(false)
                    showSnackBar(getString(R.string.common_error_message))
                }
            )
        }
    }

    private fun noEmptyField(): Boolean {
        return if (binding.edittextEmailResetPassword.editText!!.text.isNotEmpty()) {
            true
        } else {
            showSnackBar(getString(R.string.common_empty_field_message))
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}