package com.example.getfitness.ui.authentication.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentLoginBinding
import com.example.getfitness.extensions.goTo
import com.example.getfitness.extensions.safeRun
import com.example.getfitness.extensions.showSnackBar
import com.example.getfitness.model.User
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
        setsUpPopBackStack()
        setsUpRegisterText()
        setsUpForgotPasswordText()
        setsUpSubmitButton()
    }

    private fun setsUpPopBackStack() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                it.finish()
            }
        }
    }

    private fun setsUpSubmitButton() {
        val submitButton = binding.buttonSubmit
        submitButton.setOnClickListener {
            if (noEmptyFields()) {
                progressBar(true)
                tryConnectUser()
            }
        }
    }

    private fun progressBar(visibility: Boolean) {
        val progressBar = binding.progressbarLogin
        progressBar.visibility = if (visibility) View.VISIBLE else View.GONE
    }

    private fun tryConnectUser() {
        val email = binding.edittextEmailLogin.editText!!.text.toString().trim()
        val password = binding.edittextPasswordLogin.editText!!.text.toString().trim()
        val user = userFactory(email, password)
        safeRun(onOffline = { progressBar(false) }) {
            viewModel.connect(
                user,
                onSuccess = { goTo(R.id.action_loginFragment_to_feedFragment) },
                onError = {
                    progressBar(false)
                    showSnackBar(getString(R.string.fragment_login_submit_error))
                }
            )
        }
    }

    private fun userFactory(email: String, password: String): User =
        User(email = email, password = password)

    private fun noEmptyFields(): Boolean {
        return if (binding.edittextEmailLogin.editText!!.text.isNotEmpty()
            && binding.edittextPasswordLogin.editText!!.text.isNotEmpty()
        ) {
            true
        } else {
            showSnackBar(getString(R.string.common_empty_fields_message))
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