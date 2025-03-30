package com.example.electricianapp.ui.jobs
import android.graphics.Canvas
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R
import com.example.electricianapp.databinding.FragmentJobDetailBinding
import com.example.electricianapp.ui.tasks.TaskAdapter // Ensure correct import path for TaskAdapter
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * Fragment to display the details of a selected job and manage its associated tasks.
 * Provides options to edit/delete the job and add/edit/delete/complete tasks.
 */
@AndroidEntryPoint // Marks fragment for Hilt injection
class JobDetailFragment : Fragment(R.layout.fragment_job_detail) { // Use layout constructor

    private var _binding: FragmentJobDetailBinding? = null
    private val binding get() = _binding!! // Non-null accessor
    private val viewModel: JobDetailViewModel by viewModels() // Hilt injected ViewModel
    private lateinit var taskAdapter: TaskAdapter // Adapter for the tasks RecyclerView

    // Formatter for displaying dates. Consider making locale-aware.
    private val dateFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJobDetailBinding.inflate(inflater, container, false)
        Log.d("JobDetailFragment", "onCreateView called for jobId: ${viewModel.jobId}")
        // No need for setHasOptionsMenu(true) when using MenuProvider API
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("JobDetailFragment", "onViewCreated called.")
        setupMenu() // Setup ActionBar menu using modern MenuProvider API
        setupRecyclerView()
        observeViewModel()
        setupItemTouchHelper() // Setup swipe-to-delete functionality for the tasks list

        // FAB click listener to navigate to AddEditTaskFragment (in 'add' mode)
        binding.fabAddTask.setOnClickListener {
            Log.d("JobDetailFragment", "Add Task FAB clicked.")
            // Create the navigation action, passing the current jobId and -1 for taskId
            val action = JobDetailFragmentDirections.actionJobDetailFragmentToAddEditTaskFragment(
                jobId = viewModel.jobId,
                taskId = -1L // Indicate adding a new task
            )
            findNavController().safeNavigate(action) // Use safe navigation helper
        }
    }

    /** Configures the RecyclerView for displaying the list of tasks. */
    private fun setupRecyclerView() {
         Log.d("JobDetailFragment", "Setting up RecyclerView.")
         // Initialize TaskAdapter, providing lambdas for item clicks and checkbox changes
         taskAdapter = TaskAdapter(
            onTaskClick = { task ->
                 Log.d("JobDetailFragment", "Task item clicked: ID ${task.id}")
                 // Navigate to AddEditTaskFragment in 'edit' mode, passing job and task IDs
                 val action = JobDetailFragmentDirections.actionJobDetailFragmentToAddEditTaskFragment(
                     jobId = viewModel.jobId,
                     taskId = task.id // Pass the specific task ID to edit
                 )
                 findNavController().safeNavigate(action)
            },
            onTaskCheckedChange = { task, isChecked ->
                 Log.d("JobDetailFragment", "Task checkbox changed: ID ${task.id}, isChecked: $isChecked")
                 // Notify the ViewModel about the status change
                 viewModel.updateTaskStatus(task, isChecked)
            }
         )
        // Apply the adapter and layout manager to the RecyclerView
        binding.recyclerViewTasks.apply {
            adapter = taskAdapter
            layoutManager = LinearLayoutManager(requireContext())
            // Optional: Add dividers for visual separation
            // addItemDecoration(DividerItemDecoration(requireContext(), LinearLayoutManager.VERTICAL))
        }
    }

    /** Observes LiveData from the ViewModel to update the UI elements. */
    private fun observeViewModel() {
        Log.d("JobDetailFragment", "Setting up observers.")
        // Observe Job details
        viewModel.job.observe(viewLifecycleOwner) { job ->
            if (job != null) {
                Log.d("JobDetailFragment", "Job LiveData updated: Title ${job.title}")
                // Update UI with the loaded job data
                binding.textViewJobDetailTitle.text = job.title
                binding.textViewJobDetailDate.text = getString(R.string.job_date_prefix, dateFormatter.format(Date(job.date)))
                binding.textViewJobDetailAddress.text = getString(R.string.job_address_prefix, job.siteAddress ?: getString(R.string.not_available))
                // Use takeUnless to handle blank descriptions gracefully
                binding.textViewJobDetailDescription.text = job.description.takeUnless { it.isNullOrBlank() } ?: getString(R.string.no_description)
                binding.textViewJobDetailStatus.text = getString(R.string.job_status_prefix, job.status.name)
                // Dynamically update the ActionBar title
                 (activity as? AppCompatActivity)?.supportActionBar?.title = job.title
            } else {
                 // Handle the case where the observed job becomes null (e.g., deleted elsewhere)
                 Log.w("JobDetailFragment", "Observed Job data is null. Job might have been deleted.")
                 // Optionally, show a message and navigate back if the job disappears
                 // Toast.makeText(context, "Job not found.", Toast.LENGTH_SHORT).show()
                 // findNavController().popBackStack()
            }
        }

        // Observe the list of Tasks associated with the job
        viewModel.tasks.observe(viewLifecycleOwner) { tasks ->
            Log.d("JobDetailFragment", "Tasks LiveData updated. Count: ${tasks?.size ?: 0}")
            taskAdapter.submitList(tasks) // Update the RecyclerView
             // Show/hide the empty tasks message
             binding.textViewEmptyTasks.visibility = if (tasks.isNullOrEmpty()) View.VISIBLE else View.GONE
        }

        // Observe single-shot events (navigation, errors, snackbars) using repeatOnLifecycle
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                // Observe navigation events (typically after deletion)
                launch {
                    viewModel.navigateBackEvent.collect { event ->
                        event.getContentIfNotHandled()?.let {
                            Log.i("JobDetailFragment", "NavigateBackEvent received.")
                            // Show confirmation and navigate back to the previous screen (Job List)
                            Toast.makeText(context, R.string.job_deleted_success, Toast.LENGTH_SHORT).show()
                            findNavController().popBackStack()
                        }
                    }
                }
                // Observe error events
                launch {
                    viewModel.showErrorEvent.collect { event ->
                        event.getContentIfNotHandled()?.let { message ->
                            Log.e("JobDetailFragment", "ShowErrorEvent received: $message")
                            // Display error messages using a Snackbar for less intrusive feedback
                            Snackbar.make(binding.root, message, Snackbar.LENGTH_LONG).show()
                        }
                    }
                }
                 // Observe informational snackbar events
                launch {
                     viewModel.showSnackbarEvent.collect { event ->
                         event.getContentIfNotHandled()?.let { message ->
                             Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
                         }
                     }
                }
            }
        }
    }

     // --- Options Menu Handling using MenuProvider (Lifecycle-Aware) ---
    private fun setupMenu() {
         val menuHost: MenuHost = requireActivity() // Get the MenuHost
         menuHost.addMenuProvider(object : MenuProvider {
             override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                 // Inflate the specific menu resource file for this fragment
                 menuInflater.inflate(R.menu.job_detail_menu, menu)
                 Log.d("JobDetailFragment", "OptionsMenu created.")
             }

             override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                 // Handle actions based on the selected menu item ID
                 return when (menuItem.itemId) {
                     R.id.action_edit_job -> {
                         Log.d("JobDetailFragment", "Edit Job menu item selected.")
                         // Navigate to AddEditJobFragment in EDIT mode
                         viewModel.job.value?.let { currentJob -> // Get current job data safely
                             // Ensure the navigation action and arguments are defined in nav_graph.xml
                             val action = JobDetailFragmentDirections.actionJobDetailFragmentToAddEditJobFragment(
                                 userId = currentJob.userId, // Pass userId needed by AddEditJobViewModel
                                 jobId = currentJob.id       // Pass jobId to indicate edit mode
                             )
                             findNavController().safeNavigate(action) // Use safe navigation
                         } ?: run {
                             // Log and inform user if job data isn't loaded yet
                             Log.w("JobDetailFragment", "Edit Job failed: Current job data is null.")
                             Toast.makeText(context, "Cannot edit job: data not loaded.", Toast.LENGTH_SHORT).show()
                         }
                         true // Consume the event
                     }
                     R.id.action_delete_job -> {
                         Log.d("JobDetailFragment", "Delete Job menu item selected.")
                         showDeleteConfirmationDialog() // Show confirmation dialog
                         true // Consume the event
                     }
                     else -> false // Event not handled, allow other components to process
                 }
             }
         }, viewLifecycleOwner, Lifecycle.State.RESUMED) // Menu is active when fragment is RESUMED
    }

    /** Displays an AlertDialog to get user confirmation before deleting the job. */
    private fun showDeleteConfirmationDialog() {
         AlertDialog.Builder(requireContext())
            .setTitle(R.string.delete_job_confirmation_title)
            .setMessage(R.string.delete_job_confirmation_message)
            .setPositiveButton(R.string.delete) { _, _ ->
                // User confirmed deletion, tell the ViewModel to proceed
                Log.d("JobDetailFragment", "Delete confirmed by user for jobId: ${viewModel.jobId}")
                viewModel.deleteJob()
            }
            .setNegativeButton(R.string.cancel, null) // Dismisses the dialog
            .show()
    }
    // --- End Options Menu Handling ---

     // --- ItemTouchHelper for implementing swipe-to-delete on the tasks list ---
     private fun setupItemTouchHelper() {
         Log.d("JobDetailFragment", "Setting up ItemTouchHelper for tasks.")
         val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(
             0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT // Enable swiping left and right
         ) {
             // onMove is not needed for swipe-only functionality
             override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                 return false
             }

             // Called when an item is swiped completely off-screen
             override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                 val position = viewHolder.bindingAdapterPosition // Use safe adapter position
                 if (position != RecyclerView.NO_POSITION) { // Check if position is valid
                     val taskToDelete = taskAdapter.currentList[position] // Get the task from adapter's current list
                     Log.d("JobDetailFragment", "Task swiped for deletion: ID ${taskToDelete.id}, Position: $position")
                     viewModel.deleteTask(taskToDelete) // Notify ViewModel to delete the task

                     // Show a Snackbar confirmation with an Undo option
                     Snackbar.make(binding.root, R.string.task_deleted, Snackbar.LENGTH_LONG)
                         .setAction(R.string.undo) {
                             // If Undo is clicked, tell ViewModel to re-save the task
                             Log.d("JobDetailFragment", "Undo delete requested for task ID ${taskToDelete.id}")
                             viewModel.saveTaskForUndo(taskToDelete)
                         }
                         .show()
                 } else {
                     Log.w("JobDetailFragment", "Swipe detected but adapter position is NO_POSITION.")
                 }
             }

             // Optional: Customize drawing behavior during swipe (e.g., background color, icon)
             override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                 // Example: Draw a red background when swiping
                 try {
                     val itemView = viewHolder.itemView
                     val background = ColorDrawable(ContextCompat.getColor(requireContext(), R.color.status_cancelled)) // Use color resource

                     if (dX > 0) { // Swiping Right
                         background.setBounds(itemView.left, itemView.top, itemView.left + dX.toInt(), itemView.bottom)
                     } else if (dX < 0) { // Swiping Left
                         background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                     } else { // View is not actively being swiped (or swipe is canceled)
                         background.setBounds(0, 0, 0, 0)
                     }
                     background.draw(c)

                     // TODO: Optionally draw an icon (e.g., trash can) over the background here
                     // val icon = ContextCompat.getDrawable(requireContext(), R.drawable.ic_delete)
                     // icon?.setBounds(...)
                     // icon?.draw(c)

                 } catch (e: Exception) {
                      Log.e("ItemTouchHelper", "Error during onChildDraw", e)
                 }

                 // Call super to allow default swipe behavior (moving the item view)
                 super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
             }
         }
         // Attach the ItemTouchHelper to the tasks RecyclerView
         ItemTouchHelper(itemTouchHelperCallback).attachToRecyclerView(binding.recyclerViewTasks)
     }
     // --- End ItemTouchHelper ---

     /** Cleans up ViewBinding and adapter references when the fragment's view is destroyed. */
     override fun onDestroyView() {
        super.onDestroyView()
        Log.d("JobDetailFragment", "onDestroyView called.")
        // Important: Remove adapter reference from RecyclerView to prevent memory leaks
        // Especially important if the adapter holds references back to the fragment or context.
        binding.recyclerViewTasks.adapter = null
        _binding = null // Nullify the binding object
     }
}
