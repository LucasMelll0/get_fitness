package com.example.getfitness.ui.authentication.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentRegisterBinding
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.model.User
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel


class RegisterFragment : Fragment() {

    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: RegisterViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpToolbar()
        setsUpRegisterButton()
    }

    private fun setsUpRegisterButton() {
        val registerButton = binding.buttonSubmitRegister
        registerButton.setOnClickListener {
            if (noEmptyFields() && samePasswords()) {
                registerUser()
            }
        }
    }

    private fun registerUser() {
        val name = binding.edittextUserNameRegister.editText!!.text.toString().trim()
        val email = binding.edittextEmailRegister.editText!!.text.toString().trim()
        val password = binding.edittextPasswordRegister.editText!!.text.toString().trim()
        val user = userFactory(name, email, password)
        viewModel.register(
            user,
            onSuccess = {
                Snackbar.make(
                    requireView(),
                    getString(R.string.fragment_register_success_register_message),
                    Snackbar.LENGTH_SHORT
                ).show()
                goToBack()
            },
            onError = {
                errorHandler(it)
            },
            onOffline = {
                Snackbar.make(
                    requireView(),
                    getString(R.string.common_offline_message),
                    Snackbar.LENGTH_LONG
                ).show()
            }
        )
    }

    private fun errorHandler(error: String) {
        when {
            (error.contains("least 6 characters")) -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.error_lenght_password),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is badly")) -> {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.error_invalid_email),
                    Toast.LENGTH_SHORT
                ).show()
            }
            (error.contains("address is already")) -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.error_already_used_email),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            else -> {
                Snackbar.make(
                    requireView(),
                    getString(R.string.error_message_for_register_user),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun userFactory(name: String, email: String, password: String): User =
        User(name = name, email = email, password = password)

    private fun samePasswords(): Boolean {
        val password = binding.edittextPasswordRegister.editText!!.text.toString().trim()
        val confirmPassword =
            binding.edittextConfirmPasswordRegister.editText!!.text.toString().trim()
        return if (password == confirmPassword) {
            true
        } else {
            Snackbar.make(
                requireView(),
                getString(R.string.fragment_register_password_not_same),
                Snackbar.LENGTH_SHORT
            ).show()
            false
        }
    }

    private fun noEmptyFields(): Boolean {
        return if (binding.edittextUserNameRegister.editText!!.text.isNotEmpty()
            && binding.edittextEmailRegister.editText!!.text.isNotEmpty()
            && binding.edittextPasswordRegister.editText!!.text.isNotEmpty()
            && binding.edittextConfirmPasswordRegister.editText!!.text.isNotEmpty()
        ) {
            true
        } else {
            Snackbar.make(
                requireView(),
                getString(R.string.common_empty_fields_message),
                Snackbar.LENGTH_SHORT
            ).show()
            false
        }
    }


    private fun setsUpToolbar() {
        val toolbar = binding.toolbarRegister
        toolbar.setNavigationOnClickListener {
            goToBack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}