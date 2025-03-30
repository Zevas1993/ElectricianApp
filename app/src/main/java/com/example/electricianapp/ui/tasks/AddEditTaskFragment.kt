package com.example.electricianapp.ui.tasks
import android.os.Bundle
import android.util.Log
import android.view.*
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
import com.example.electricianapp.databinding.FragmentAddEditTaskBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

/**
 * Fragment for adding a new task or editing an existing one associated with a specific job.
 * Takes user input for task description and completion status.
 */
@AndroidEntryPoint // Marks fragment for Hilt injection
class AddEditTaskFragment : Fragment(R.layout.fragment_add_edit_task) { // Use layout constructor

    private var _binding: FragmentAddEditTaskBinding? = null
    private val binding get() = _binding!! // Non-null accessor for binding
    private val viewModel: AddEditTaskViewModel by viewModels() // Hilt injected ViewModel

    // Flag to prevent repopulating fields on config change after user edits
    private var fieldsPopulated = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentAddEditTaskBinding.inflate(inflater, container, false)
        Log.d("AddEditTaskFragment", "onCreateView called. isEditMode: ${viewModel.isEditMode}, JobId: ${viewModel.jobId}")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("AddEditTaskFragment", "onViewCreated called.")

        setupListeners()
        observeViewModelState()

        // Set the ActionBar title based on whether adding or editing
        val actionBarTitleRes = if (viewModel.isEditMode) R.string.edit_task_title else R.string.add_task_title
        (activity as? AppCompatActivity)?.supportActionBar?.title = getString(actionBarTitleRes)
    }

    /** Sets up listeners for the save button and input field validation. */
    private fun setupListeners() {
        // Listener for the Save Task button
        binding.buttonSaveTask.setOnClickListener {
            Log.d("AddEditTaskFragment", "Save Task button clicked.")
            // Get current values from the input fields
            val description = binding.editTextTaskDescription.text.toString()
            val isCompleted = binding.checkBoxTaskFormCompleted.isChecked
            // Ask the ViewModel to save the task
            viewModel.saveTask(description, isCompleted)
        }

         // Add a text change listener to the description field to clear any error message
         // when the user starts typing.
         binding.editTextTaskDescription.doOnTextChanged { _, _, _, _ ->
             if (binding.textFieldTaskDescription.error != null) {
                 binding.textFieldTaskDescription.error = null // Clear the error
             }
         }
    }

    /** Observes LiveData from the ViewModel to populate the form and handle events. */
    private fun observeViewModelState() {
        Log.d("AddEditTaskFragment", "Setting up observers.")
        // Observe the 'task' LiveData to populate the form fields when editing.
        // This runs only once if the fieldsPopulated flag is used correctly.
        viewModel.task.observe(viewLifecycleOwner) { task ->
            // Populate only if task data is available and fields haven't been populated yet
            if (task != null && !fieldsPopulated) {
                Log.d("AddEditTaskFragment", "Populating form fields for task ID: ${task.id}")
                binding.editTextTaskDescription.setText(task.description)
                binding.checkBoxTaskFormCompleted.isChecked = task.isCompleted
                fieldsPopulated = true // Mark fields as populated
            } else if (task == null && viewModel.isEditMode && !fieldsPopulated) {
                // Log warning if in edit mode but task data failed to load
                Log.w("AddEditTaskFragment", "In Edit mode, but received null task data.")
                // Optionally disable save or show a persistent error message
            }
        }

        // Observe single-shot events (navigation, errors) using repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe navigation event (triggered after successful save)
                launch {
                    viewModel.navigateBackEvent.collect { event ->
                        event.getContentIfNotHandled()?.let {
                            Log.i("AddEditTaskFragment", "NavigateBackEvent collected, navigating back.")
                            findNavController().popBackStack() // Navigate back to the previous screen (Job Detail)
                        }
                    }
                }
                 // Observe error event
                 launch {
                    viewModel.showErrorEvent.collect { event ->
                        event.getContentIfNotHandled()?.let { message ->
                            Log.w("AddEditTaskFragment", "ShowErrorEvent collected: $message")
                            // Display the error message. Prioritize setting it on the description field.
                            if (message.contains("description", ignoreCase = true) && _binding != null) {
                                 binding.textFieldTaskDescription.error = message
                                 binding.textFieldTaskDescription.requestFocus() // Focus the field
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
        Log.d("AddEditTaskFragment", "onDestroyView called.")
        _binding = null // Avoid memory leaks by nullifying the binding reference
        fieldsPopulated = false // Reset flag for potential recreation
    }
}
