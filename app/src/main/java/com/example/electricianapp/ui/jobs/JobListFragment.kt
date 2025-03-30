package com.example.electricianapp.ui.jobs
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentJobListBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn // For sign out
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import dagger.hilt.android.AndroidEntryPoint
// NOTE: No need to inject FirebaseAuth here if sign-out logic is fully in ViewModel and initiated via viewModel.signOut()

/**
 * Fragment displaying the list of jobs for the logged-in user.
 * Allows adding new jobs via a FAB and logging out via an options menu.
 */
@AndroidEntryPoint // Marks fragment for Hilt injection
class JobListFragment : Fragment(R.layout.fragment_job_list) { // Use layout constructor

    private var _binding: FragmentJobListBinding? = null
    private val binding get() = _binding!! // Non-null accessor for the binding
    private val viewModel: JobListViewModel by viewModels() // Hilt injected ViewModel
    private lateinit var jobAdapter: JobAdapter

    // Google Sign In Client needed to perform sign-out from Google services.
    private lateinit var googleSignInClient: GoogleSignInClient

    // Use onCreate to initialize non-view-dependent components like GoogleSignInClient
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d("JobListFragment", "onCreate called.")
        setupGoogleSignInClient() // Initialize Google client early
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJobListBinding.inflate(inflater, container, false)
        Log.d("JobListFragment", "onCreateView called.")
        // No need for setHasOptionsMenu(true) here when using MenuProvider approach
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JobListFragment", "onViewCreated called for user ID: ${viewModel.userId}")
        setupMenu() // Setup ActionBar menu using MenuProvider API
        setupRecyclerView()
        observeViewModel()

        // Set up FAB click listener to navigate to the AddEditJobFragment (for adding)
        binding.fabAddJob.setOnClickListener {
            Log.d("JobListFragment", "Add Job FAB clicked.")
            // Ensure the action and arguments in nav_graph.xml are correct
            // Pass the current user ID, jobId defaults to -1L in the destination definition
            val action = JobListFragmentDirections.actionJobListFragmentToAddEditJobFragment(viewModel.userId)
            findNavController().safeNavigate(action) // Use safe navigation extension
        }

        // Set the ActionBar title for this screen
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(R.string.title_jobs)
    }

     /** Initializes the GoogleSignInClient required for the sign-out process. */
     private fun setupGoogleSignInClient() {
         // Use the same options used during sign-in (at least requestEmail) to get the correct client instance.
          val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail() // Requesting email is usually part of standard setup
            .build()
        // Use requireActivity() as context, safe within onCreate/onViewCreated lifecycle
        googleSignInClient = GoogleSignIn.getClient(requireActivity(), gso)
         Log.d("JobListFragment", "GoogleSignInClient initialized.")
     }

    /** Configures the RecyclerView: sets adapter, layout manager, and item decoration. */
    private fun setupRecyclerView() {
        // Initialize the adapter, passing the navigation lambda for item clicks
        jobAdapter = JobAdapter { clickedJob ->
            Log.d("JobListFragment", "Job item clicked: ID ${clickedJob.id}")
            // Navigate to JobDetailFragment when a job item is clicked
            val action = JobListFragmentDirections.actionJobListFragmentToJobDetailFragment(clickedJob.id)
            findNavController().safeNavigate(action) // Use safe navigation
        }
        binding.recyclerViewJobs.apply {
            adapter = jobAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Add a divider line between items for better visual separation
            addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
            setHasFixedSize(true) // Optimize if item size doesn't change
        }
        Log.d("JobListFragment", "RecyclerView setup complete.")
        // Optional: Add swipe-to-delete functionality using ItemTouchHelper later
        // setupItemTouchHelper()
    }

    /** Observes LiveData from the ViewModel to update the UI (job list, user email). */
    private fun observeViewModel() {
        // Observe the list of jobs
        viewModel.jobs.observe(viewLifecycleOwner) { jobs ->
            Log.d("JobListFragment", "Job list updated. Count: ${jobs?.size ?: 0}")
            jobAdapter.submitList(jobs) // Submit the new list to the adapter for diffing
            // Show or hide the "empty list" message
            binding.textViewEmptyJobs.visibility = if (jobs.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        // Observe the user's email (optional) to display in the ActionBar subtitle
         viewModel.userEmail.observe(viewLifecycleOwner) { email ->
             Log.d("JobListFragment", "User email observed: $email")
             (activity as? AppCompatActivity)?.supportActionBar?.subtitle = email ?: ""
         }
         // TODO: Observe loading/error states from ViewModel if implemented
    }

     // --- Options Menu Handling using MenuProvider (Modern & Lifecycle-Aware) ---
    private fun setupMenu() {
         // Get the MenuHost (typically the Activity or Fragment)
         val menuHost: MenuHost = requireActivity()

         // Add the MenuProvider, linking it to the fragment's lifecycle
         menuHost.addMenuProvider(object : MenuProvider {
             // Called when the menu needs to be created
             override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                 menuInflater.inflate(R.menu.job_list_menu, menu)
                 Log.d("JobListFragment", "OptionsMenu created.")
             }

             // Called when a menu item is selected
             override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                 return when (menuItem.itemId) {
                     R.id.action_logout -> {
                         Log.d("JobListFragment", "Logout menu item selected.")
                         signOut() // Initiate the sign-out process
                         true // Consume the event
                     }
                     else -> false // Allow other components to handle the event
                 }
             }
         }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Menu is visible when fragment is RESUMED
    }

    /** Handles the complete sign-out process, including Google Client and navigation. */
    private fun signOut() {
        Log.d("JobListFragment", "signOut() called.")
        // 1. Tell the ViewModel to sign out from Firebase Authentication
        viewModel.signOut()

        // 2. Sign out from the Google Sign-In Client (important to clear Google session)
        googleSignInClient.signOut().addOnCompleteListener(requireActivity()) { task ->
            if (task.isSuccessful) {
                 Log.i("JobListFragment", "Google Sign Out successful.")
            } else {
                 // Log the error, but still proceed with navigation as Firebase sign out worked.
                 Log.w("JobListFragment", "Google Sign Out failed.", task.exception)
                 Toast.makeText(context, "Logout partially failed (Google).", Toast.LENGTH_SHORT).show()
            }
            // 3. Navigate back to the Login screen AFTER the Google Sign-Out attempt completes.
            navigateToLoginScreen()
        }
    }

    /** Navigates back to the LoginFragment, ensuring the back stack is cleared. */
    private fun navigateToLoginScreen() {
         // Use viewLifecycleOwner's scope for safety, though navigation should be quick.
         // Check if fragment is still in a valid state to navigate.
         if (isAdded && view != null) {
             try {
                 val navController = findNavController()
                 // Pop the back stack all the way up to the root of the graph (inclusive)
                 navController.popBackStack(R.id.nav_graph, true)
                 // Then navigate to the login fragment (start destination)
                 navController.navigate(R.id.loginFragment)
                 Log.i("JobListFragment", "Navigated back to LoginFragment.")
             } catch (e: Exception) {
                  // Catch potential navigation errors (e.g., if fragment is detached)
                  Log.e("JobListFragment", "Error navigating back to login screen", e)
             }
         } else {
             Log.w("JobListFragment", "Navigation to Login screen skipped: Fragment not in valid state.")
         }
    }
    // --- End Options Menu Handling ---

    /** Cleans up View Binding and RecyclerView adapter reference. */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("JobListFragment", "onDestroyView called.")
        binding.recyclerViewJobs.adapter = null // Important to avoid leaks from RecyclerView
        _binding = null // Nullify the binding object
    }
}

/**
 * Extension function for safe navigation. Prevents crashes if navigating quickly
 * or if the current destination is unexpected.
 */
fun NavController.safeNavigate(action: androidx.navigation.NavDirections) {
    try {
        currentDestination?.getAction(action.actionId)?.let {
            navigate(action)
        } ?: Log.w("safeNavigate", "Action ${action.actionId} not found on current destination ${currentDestination?.id}")
    } catch (e: Exception) {
        Log.e("safeNavigate", "Navigation failed for action ${action.actionId}", e)
    }
}

// Overload for navigating by resource ID
fun NavController.safeNavigate(resId: Int, args: Bundle? = null) {
     try {
        currentDestination?.getAction(resId)?.let {
            navigate(resId, args)
        } ?: Log.w("safeNavigate", "Action ID $resId not found on current destination ${currentDestination?.id}")
    } catch (e: Exception) {
        Log.e("safeNavigate", "Navigation failed for action ID $resId", e)
    }
}
