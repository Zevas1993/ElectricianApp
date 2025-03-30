package com.example.electricianapp.ui.jobs
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.example.electricianapp.R
import com.example.electricianapp.data.model.JobStatus
import com.example.electricianapp.databinding.FragmentAddEditJobBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for adding a new job or editing an existing one.
 * Takes user input for job details and interacts with AddEditJobViewModel.
 */
@AndroidEntryPoint // Marks fragment for Hilt injection
class AddEditJobFragment : Fragment(R.layout.fragment_add_edit_job) { // Use layout constructor

    private var _binding: FragmentAddEditJobBinding? = null
    private val binding get() = _binding!! // Non-null accessor for binding
    private val viewModel: AddEditJobViewModel by viewModels() // Hilt injected ViewModel

    // Flag to prevent populating form fields multiple times, e.g., on screen rotation
    // after the user has already started editing the prepopulated fields.
    private var fieldsPopulated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditJobBinding.inflate(inflater, container, false)
        Log.d("AddEditJobFragment", "onCreateView called. isEditMode: ${viewModel.isEditMode}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AddEditJobFragment", "onViewCreated called.")

        setupStatusDropdown() // Setup the options for the status dropdown
        setupInputListeners() // Setup listeners for save button and input validation
        observeViewModelState() // Start observing ViewModel LiveData

        // Set the ActionBar title appropriately for "Add" or "Edit" mode
        val actionBarTitleRes = if (viewModel.isEditMode) R.string.edit_job_title else R.string.add_job_title
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(actionBarTitleRes)
    }

    /** Configures the ArrayAdapter for the JobStatus Exposed Dropdown Menu. */
    private fun setupStatusDropdown() {
        // Get the string names of all JobStatus enum values
        val statusItems = JobStatus.values().map { it.name }
        // Create an ArrayAdapter using the standard dropdown item layout
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_dropdown_item, statusItems)
        // Find the AutoCompleteTextView within the TextInputLayout and set its adapter
        // Using safe cast `as?`
        (binding.textFieldJobStatus.editText as? AutoCompleteTextView)?.setAdapter(adapter)
        Log.d("AddEditJobFragment", "Status dropdown adapter set with items: $statusItems")
    }

    /** Sets up listeners for UI interactions like button clicks and text changes. */
    private fun setupInputListeners() {
        // Listener for the Save Job button
        binding.buttonSaveJob.setOnClickListener {
            Log.d("AddEditJobFragment", "Save Job button clicked.")
            // Gather data from input fields
            val title = binding.editTextJobTitle.text.toString()
            val address = binding.editTextJobAddress.text.toString()
            val description = binding.editTextJobDescription.text.toString()
            // Get the currently selected status string from the dropdown's AutoCompleteTextView
            val selectedStatusString = binding.spinnerJobStatus.text.toString()

            // Convert the selected string safely back to a JobStatus enum.
            // Default to SCHEDULED if the string is empty or doesn't match any known status.
            val status = try {
                 if (selectedStatusString.isNotEmpty()) enumValueOf<JobStatus>(selectedStatusString) else JobStatus.SCHEDULED
            } catch (e: IllegalArgumentException) {
                 Log.w("AddEditJobFragment", "Invalid status string '$selectedStatusString' selected, defaulting to SCHEDULED.")
                 JobStatus.SCHEDULED // Fallback to a default status
            }

            // Call the ViewModel to perform the save operation
            viewModel.saveJob(title, address, description, status)
        }

        // Add a text watcher to the title field to clear its error indicator when the user types.
         binding.editTextJobTitle.doOnTextChanged { _, _, _, _ ->
             // Check if an error is currently shown before clearing it
             if (binding.textFieldJobTitle.error != null) {
                 binding.textFieldJobTitle.error = null // Remove the error message
             }
         }
    }

    /** Observes LiveData from the ViewModel to update UI and handle events. */
    private fun observeViewModelState() {
        Log.d("AddEditJobFragment", "Setting up observers.")
        // Observe the 'job' LiveData. This is primarily used to populate the form fields
        // when the fragment is opened in 'edit' mode.
        viewModel.job.observe(viewLifecycleOwner) { job ->
            // Populate fields only if:
            // 1. We are in edit mode (`job` is not null).
            // 2. Fields haven't been populated yet (to avoid overwriting user changes on rotation).
            if (job != null && !fieldsPopulated) {
                Log.d("AddEditJobFragment", "Populating form fields for job ID: ${job.id}")
                binding.editTextJobTitle.setText(job.title)
                binding.editTextJobAddress.setText(job.siteAddress ?: "") // Use empty string if null
                binding.editTextJobDescription.setText(job.description ?: "")
                // Set the text of the AutoCompleteTextView for the dropdown.
                // The 'false' parameter prevents the dropdown adapter from filtering based on this initial text.
                (binding.textFieldJobStatus.editText as? AutoCompleteTextView)?.setText(job.status.name, false)
                fieldsPopulated = true // Mark that initial population is done
            } else if (job == null && viewModel.isEditMode && !fieldsPopulated) {
                // Log a warning if in edit mode but the job data failed to load.
                Log.w("AddEditJobFragment", "In Edit mode, but received null job data.")
                // Optionally disable the form or show a persistent error
            }
        }

        // Observe single-shot events (navigation, errors) using repeatOnLifecycle.
        // This ensures observers are active only when the fragment is at least STARTED.
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Coroutine to observe navigation events (triggered on successful save)
                launch {
                    viewModel.navigateBackEvent.collect { event ->
                        // Use getContentIfNotHandled to ensure navigation happens only once per event.
                        event.getContentIfNotHandled()?.let {
                            Log.i("AddEditJobFragment", "NavigateBackEvent collected, navigating back.")
                            findNavController().popBackStack() // Navigate back to the previous screen
                        }
                    }
                }
                // Coroutine to observe error events
                launch {
                    viewModel.showErrorEvent.collect { event ->
                        event.getContentIfNotHandled()?.let { message ->
                            Log.w("AddEditJobFragment", "ShowErrorEvent collected: $message")
                            // Display the error message. Try setting it on the relevant input field first.
                            if (message.contains("title", ignoreCase = true) && _binding != null) {
                                binding.textFieldJobTitle.error = message // Show error specific to title
                                binding.textFieldJobTitle.requestFocus() // Focus the field with error
                            } else {
                                // Show general errors using a Toast
                                Toast.makeText(context, message, Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
            }
        }
    }

    /** Cleans up the ViewBinding reference when the fragment's view is destroyed. */
    override fun onDestroyView() {
        super.onDestroyView()
        Log.d("AddEditJobFragment", "onDestroyView called.")
        _binding = null // Nullify the binding object to avoid memory leaks
        fieldsPopulated = false // Reset flag if fragment is recreated
    }
}
