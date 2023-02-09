package com.example.getfitness.ui.authentication.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentRegisterBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.goToBack
import com.example.getfitness.extensions.safeRun
import com.example.getfitness.extensions.showSnackBar
import com.example.getfitness.model.User
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
                progressBar(true)
                tryRegisterUser()
            }
        }
    }

    private fun progressBar(visible: Boolean) {
        val progressBar = binding.progressbarRegister
        progressBar.visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun tryRegisterUser() {
        val name = binding.edittextUserNameRegister.editText!!.text.toString().trim()
        val email = binding.edittextEmailRegister.editText!!.text.toString().trim()
        val password = binding.edittextPasswordRegister.editText!!.text.toString().trim()
        val user = userFactory(name, email, password)
        safeRun(onOffline = { progressBar(false) }) {
            viewModel.register(
                user,
                onSuccess = {
                    progressBar(false)
                    showSnackBar(getString(R.string.fragment_register_success_register_message))
                    goTo(R.id.action_registerFragment_to_welcomeScreenFragment)
                },
                onError = {
                    progressBar(false)
                    errorHandler(it)
                }
            )
        }
    }

    private fun errorHandler(error: String) {
        when {
            (error.contains("least 6 characters")) -> {
                showSnackBar(getString(R.string.error_lenght_password))
            }
            (error.contains("address is badly")) -> {
                showSnackBar(getString(R.string.error_invalid_email))
            }
            (error.contains("address is already")) -> {
                showSnackBar(getString(R.string.error_already_used_email))
            }
            else -> {
                showSnackBar(getString(R.string.error_message_for_register_user))
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
            showSnackBar(getString(R.string.fragment_register_password_not_same))
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
            showSnackBar(getString(R.string.common_empty_fields_message))
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