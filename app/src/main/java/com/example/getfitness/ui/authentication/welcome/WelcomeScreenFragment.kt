package com.example.getfitness.ui.authentication.welcome

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import com.example.getfitness.R
import com.example.getfitness.databinding.FragmentWelcomeScreenBinding
import com.example.getfitness.extensions.goTo
import com.google.firebase.auth.FirebaseAuth

class WelcomeScreenFragment : Fragment() {

    private var _binding: FragmentWelcomeScreenBinding? = null
    private val binding get() = _binding!!
    private val currentUser = FirebaseAuth.getInstance().currentUser

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWelcomeScreenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setsUpPopBackStack()
        fillUi()
        setsUpButtonGoToHome()
    }

    private fun setsUpButtonGoToHome() {
        val buttonGoToHome = binding.buttonGoToHome
        buttonGoToHome.setOnClickListener {
            goTo(R.id.action_welcomeScreenFragment_to_feedFragment)
        }
    }

    private fun fillUi() {
        val textviewWelcomeMessage = binding.textviewWelcomeMessage
        currentUser?.let {
            textviewWelcomeMessage.text = getString(R.string.fragment_welcome_message, it.displayName?.trim())
        }
    }

    private fun setsUpPopBackStack() {
        activity?.let {
            it.onBackPressedDispatcher.addCallback(this) {
                it.finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}