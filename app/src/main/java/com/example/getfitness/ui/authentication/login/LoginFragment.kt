package com.example.getfitness.ui.authentication.login

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentLoginBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.model.User
import com.google.android.material.snackbar.Snackbar
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: LoginViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpRegisterText()
        setsUpForgotPasswordText()
        setsUpSubmitButton()
    }

    private fun setsUpSubmitButton() {
        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {
            if (noEmptyFields()) {
                connectUser()
            }
        }
    }

    private fun connectUser() {
        val email = binding.edittextEmailLogin.editText!!.text.toString().trim()
        val password = binding.edittextPasswordLogin.editText!!.text.toString().trim()
        val user = userFactory(email, password)
        viewModel.connect(
            user,
            onSuccess = { Log.i("Teste", "connectUser: SUCESSO") },
            onError = {
                Snackbar.make(
                    requireView(),
                    getString(R.string.common_error_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            },
            onOffline = {
                Snackbar.make(
                    requireView(),
                    getString(R.string.common_offline_message),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
            )
    }

    private fun userFactory(email: String, password: String): User =
        User(email = email, password = password)

    private fun noEmptyFields(): Boolean {
        return if (binding.edittextEmailLogin.editText!!.text.isNotEmpty()
            && binding.edittextPasswordLogin.editText!!.text.isNotEmpty()
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

    private fun setsUpRegisterText() {
        val textRegister = binding.textviewNoAccountQuestion
        textRegister.setOnClickListener {
            goTo(R.id.action_loginFragment_to_registerFragment)
        }
    }

    private fun setsUpForgotPasswordText() {
        val textForgot = binding.textviewForgotPassword
        textForgot.setOnClickListener {
            goTo(R.id.action_loginFragment_to_resetPasswordFragment)
        }
    }
}