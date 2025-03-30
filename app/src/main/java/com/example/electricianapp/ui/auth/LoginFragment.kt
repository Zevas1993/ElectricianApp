package com.example.electricianapp.ui.auth
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.view.isVisible // Use isVisible extension function for Views
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.example.electricianapp.R // Import project's R class for resources
import com.example.electricianapp.databinding.FragmentLoginBinding // ViewBinding class
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.common.api.CommonStatusCodes // For specific error codes
import com.google.android.gms.tasks.Task
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment responsible for the user login screen.
 * Handles user input for traditional login (placeholder) and initiates Google Sign-In flow.
 * Observes the LoginViewModel for state changes and navigation events.
 */
@AndroidEntryPoint // Marks fragment for Hilt dependency injection
class LoginFragment : Fragment(R.layout.fragment_login) { // Use layout constructor for simpler setup

    private var _binding: FragmentLoginBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    // Obtain ViewModel instance via Hilt
    private val viewModel: LoginViewModel by viewModels()

    // Google Sign-In client and launcher for handling the sign-in activity result
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>

    // ** VERY IMPORTANT: Replace this placeholder with your actual Web Client ID from Firebase Console **
    private val WEB_CLIENT_ID = "YOUR_WEB_CLIENT_ID.apps.googleusercontent.com"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        // Inflate the layout using ViewBinding
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        Log.d("LoginFragment", "onCreateView called")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("LoginFragment", "onViewCreated called")

        // ** Critical Check:** Remind developer to replace the placeholder ID.
        // Do this check early in onViewCreated.
        if (WEB_CLIENT_ID.startsWith("YOUR_WEB_CLIENT_ID")) {
            Log.e("LoginFragment", "CRITICAL ERROR: WEB_CLIENT_ID is not set in LoginFragment.kt. Replace the placeholder with your actual ID from Firebase Console.")
            // Show a persistent error and disable Google sign-in to prevent crashes.
            Toast.makeText(context, "Configuration Error: Google Sign-In disabled. See Logcat.", Toast.LENGTH_LONG).show()
            binding.googleSignInButton.isEnabled = false
        } else {
            // Only setup Google Sign-In if the Client ID seems configured.
            setupGoogleSignIn()
        }

        setupClickListeners()
        observeViewModel()

        // Check initial state from ViewModel (handles cases where user is already logged in on app start)
        handleInitialViewModelState()
    }

    /** Configures the GoogleSignInClient and registers the ActivityResultLauncher. */
    private fun setupGoogleSignIn() {
         Log.d("LoginFragment", "Setting up Google Sign-In")
         // Configure Google Sign-In options. Requesting the ID token is essential for Firebase Auth.
         val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID) // Request ID token from Google
            .requestEmail() // Request user's email address
            .build()

        // Get the GoogleSignInClient instance.
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)

        // Register the activity result launcher. This replaces the deprecated onActivityResult.
        googleSignInLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult() // Standard contract for starting an activity for result
        ) { result ->
            // This lambda block is executed when the Google Sign-In activity returns a result.
            Log.d("LoginFragment", "Google Sign-In Activity Result received.")
            // Hide the progress bar now that the external activity has finished.
            binding.progressBarLogin.isVisible = false
            // Get the sign-in task from the returned intent data.
            val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
            // Process the result of the sign-in attempt.
            handleSignInResult(task)
        }
    }

    /** Sets up onClick listeners for the login buttons. */
    private fun setupClickListeners() {
        binding.buttonLogin.setOnClickListener {
            val username = binding.editTextUsername.text.toString().trim()
            val password = binding.editTextPassword.text.toString().trim()
            // Basic client-side validation.
            if(username.isNotEmpty() && password.isNotEmpty()) {
                 Log.d("LoginFragment", "Traditional login button clicked.")
                 viewModel.loginUser(username, password) // Trigger ViewModel's login logic
            } else {
                 showError(getString(R.string.error_empty_credentials))
            }
        }

        // Only enable Google Sign-In click if the button itself is enabled (i.e., client ID is set).
        if (binding.googleSignInButton.isEnabled) {
            binding.googleSignInButton.setOnClickListener {
                Log.d("LoginFragment", "Google Sign-In button clicked.")
                signInWithGoogle() // Initiate the Google Sign-In flow
            }
        }
        // Set standard button size recommended by Google.
        binding.googleSignInButton.setSize(com.google.android.gms.common.SignInButton.SIZE_WIDE)
    }

    /** Creates and launches the Google Sign-In intent using the ActivityResultLauncher. */
    private fun signInWithGoogle() {
         binding.progressBarLogin.isVisible = true // Show loading indicator
         val signInIntent = googleSignInClient.signInIntent // Get the Intent from the client
         try {
            googleSignInLauncher.launch(signInIntent) // Launch the activity for result
            Log.d("LoginFragment", "Google Sign-In Intent launched.")
         } catch (e: Exception) {
             // Catch potential exceptions during launch (e.g., ActivityNotFoundException)
             Log.e("LoginFragment", "Error launching Google Sign-In intent", e)
             showError("Could not start Google Sign-In process.")
             binding.progressBarLogin.isVisible = false
         }
    }

     /**
      * Processes the result received from the Google Sign-In activity.
      * If successful, extracts the ID token and passes it to the ViewModel for Firebase authentication.
      * Handles various Google Sign-In specific errors.
      */
     private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            // Attempt to get the account result. Throws ApiException on failure.
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)

            // Google Sign-In was successful, extract the ID Token.
            val idToken = account.idToken
            if (idToken != null) {
                Log.i("LoginFragment", "Google Sign-In successful. ID Token obtained. Authenticating with Firebase...")
                // Pass the token to the ViewModel to authenticate with Firebase.
                viewModel.signInWithGoogleCredential(idToken)
            } else {
                // This scenario is less common if getResult succeeded, but handle defensively.
                Log.e("LoginFragment", "Google Sign-In successful but ID Token is null.")
                showError(getString(R.string.error_google_no_token))
            }

        } catch (e: ApiException) {
            // Google Sign-In failed. Log the status code and provide user feedback.
            Log.w("LoginFragment", "Google Sign-In failed with status code: ${e.statusCode} (${CommonStatusCodes.getStatusCodeString(e.statusCode)})")
             when (e.statusCode) {
                 CommonStatusCodes.NETWORK_ERROR -> showError("Network error during Google Sign-In. Please check connection.")
                 CommonStatusCodes.SIGN_IN_CANCELLED, 12501 /* Also SIGN_IN_CANCELLED */ -> {
                     // User explicitly cancelled the sign-in flow (e.g., pressed back). No error message needed.
                     Log.i("LoginFragment", "Google Sign-In cancelled by user.")
                 }
                 CommonStatusCodes.SIGN_IN_REQUIRED -> {
                     // The user needs to sign in (not signed in before or session expired).
                     // This state might be reached if trying silent sign-in, but here it's less expected after explicit button press.
                     Log.w("LoginFragment", "Google Sign-In required, but flow was already initiated?")
                     showError("Google Sign-In is required.")
                 }
                 // Add other specific CommonStatusCodes if needed (e.g., RESOLUTION_REQUIRED)
                 else -> {
                     // Handle other specific API errors or show a generic message.
                     showError(getString(R.string.error_google_signin_failed_code, e.statusCode))
                 }
             }
        } catch (e: Exception) {
            // Catch any other unexpected exceptions during result processing.
            Log.e("LoginFragment", "Unexpected exception processing Google Sign-In result", e)
             showError(getString(R.string.error_google_signin_failed_generic))
        }
    }

    /** Observes the ViewModel's LiveData for login state changes and events. */
    private fun observeViewModel() {
        // Use repeatOnLifecycle to ensure observation is active only when fragment is STARTED.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.loginResult.observe(viewLifecycleOwner) { event ->
                    // Peek content first to update UI state like progress bar visibility
                    // This avoids issues if the event was already handled but loading state needs update.
                    val result = event.peekContent()
                    if (_binding != null) { // Check binding is not null before accessing views
                         binding.progressBarLogin.isVisible = result is LoginResult.Loading
                    }

                    // Consume the event only once for actions like navigation or showing toasts.
                    event.getContentIfNotHandled()?.let { loginResult ->
                        Log.d("LoginFragment", "LoginResult event received: ${loginResult::class.java.simpleName}")
                        when (loginResult) {
                            is LoginResult.Success -> {
                                Log.i("LoginFragment", "Login Successful for user ID: ${loginResult.user.id}. Navigating.")
                                // Show success message and navigate to the main part of the app.
                                Toast.makeText(context, R.string.login_successful, Toast.LENGTH_SHORT).show()
                                navigateToJobList(loginResult.user.id)
                            }
                            is LoginResult.Error -> {
                                Log.w("LoginFragment", "Login Error: ${loginResult.message}")
                                // Show the error message provided by the ViewModel.
                                showError(loginResult.message)
                            }
                            is LoginResult.Loading -> {
                                // UI update (progress bar visibility) is handled above using peekContent.
                                Log.d("LoginFragment", "Login state: Loading")
                            }
                        }
                    }
                }
            }
        }
    }

     /** Checks the initial state from ViewModel in case login happened during ViewModel init. */
     private fun handleInitialViewModelState() {
        viewModel.loginResult.value?.peekContent()?.let { initialResult ->
             when (initialResult) {
                 is LoginResult.Success -> {
                     // If already successful (likely from ViewModel init check), navigate immediately.
                     Log.d("LoginFragment", "Initial state is Success, navigating.")
                     navigateToJobList(initialResult.user.id)
                 }
                 is LoginResult.Loading -> {
                     // If initial state is Loading, ensure progress bar is visible.
                     Log.d("LoginFragment", "Initial state is Loading.")
                     binding.progressBarLogin.isVisible = true
                 }
                 is LoginResult.Error -> {
                     // If initial state was an error (less likely, but possible), show it.
                     Log.w("LoginFragment", "Initial state is Error: ${initialResult.message}")
                     showError(initialResult.message) // Show initial error if needed
                 }
             }
        }
     }


    /** Safely navigates to the Job List screen if the Fragment is in a valid state. */
    private fun navigateToJobList(userId: Long) {
        // Check if the fragment is still attached to its context and the current
        // navigation destination is still the LoginFragment to prevent navigation errors.
        if (isAdded && view != null) { // Check isAdded and view lifecycle
             try {
                val navController = findNavController()
                if (navController.currentDestination?.id == R.id.loginFragment) {
                    val action = LoginFragmentDirections.actionLoginFragmentToJobListFragment(userId)
                    navController.navigate(action)
                    Log.i("LoginFragment", "Navigation to JobListFragment triggered for user ID: $userId")
                } else {
                    // Avoid navigating if already navigated or in a different state.
                    Log.w("LoginFragment", "Skipping navigation to JobList: Current destination is not LoginFragment (ID: ${navController.currentDestination?.id})")
                }
             } catch (e: IllegalStateException) {
                  // Catch error if NavController is not available (fragment detached quickly)
                  Log.e("LoginFragment", "Error getting NavController during navigation", e)
             } catch (e: IllegalArgumentException) {
                  // Catch error if the navigation action is invalid
                  Log.e("LoginFragment", "Error navigating to JobListFragment", e)
             }
        } else {
             Log.w("LoginFragment", "Skipping navigation to JobList: Fragment not added or view destroyed.")
        }
    }

    /** Displays an error message using a Toast and ensures the progress bar is hidden. */
    private fun showError(message: String) {
        // Check if fragment is still attached to context before showing UI elements.
        context?.let { // Use context safely
            Toast.makeText(it, message, Toast.LENGTH_LONG).show()
        }
        // Ensure binding is not null before accessing views, especially if called asynchronously.
        if (_binding != null) {
             binding.progressBarLogin.isVisible = false
        }
    }

    /** Cleans up the ViewBinding reference when the fragment's view is destroyed to prevent memory leaks. */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("LoginFragment", "onDestroyView called")
        _binding = null // Nullify the binding object
    }
}
