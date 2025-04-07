package com.example.electricianapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    // TODO: Inject ViewModel if needed for login logic

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // TODO: Implement login button click listener
        // binding.loginButton.setOnClickListener { handleLogin() }

        // TODO: Implement Google Sign-In button click listener
        // binding.googleSignInButton.setOnClickListener { handleGoogleSignIn() }

        // Placeholder navigation for testing - replace with actual login success logic
        binding.buttonLogin.setOnClickListener { // Corrected ID
             // Assuming successful login, navigate to home fragment
             // Replace '1L' with the actual user ID after successful authentication
             // val action = LoginFragmentDirections.actionLoginFragmentToJobListFragment(userId = 1L) // Old action
             val action = LoginFragmentDirections.actionLoginFragmentToHomeFragment() // Corrected action
             findNavController().navigate(action)
        }

    }

    // TODO: Implement handleLogin() function
    // TODO: Implement handleGoogleSignIn() function and ActivityResultLauncher setup

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
