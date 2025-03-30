package com.example.electricianapp.ui.tasks // Ensure package name is correct
import android.graphics.Paint // For strike-through text
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.data.model.TaskEntity
import com.example.electricianapp.databinding.ItemTaskBinding // Ensure correct binding class import

/**
 * RecyclerView Adapter for displaying a list of TaskEntity objects within the JobDetail screen.
 * Uses ListAdapter with DiffUtil for efficient UI updates.
 * Handles clicks on the task item (for editing) and changes to the completion checkbox.
 */
class TaskAdapter(
    // Lambda function invoked when the task item itself (e.g., the text) is clicked.
    private val onTaskClick: (TaskEntity) -> Unit,
    // Lambda function invoked when the task's completed checkbox state is changed by the user.
    private val onTaskCheckedChange: (TaskEntity, Boolean) -> Unit
) : ListAdapter<TaskEntity, TaskAdapter.TaskViewHolder>(DiffCallback) {

    /** Creates new ViewHolders by inflating the item layout (item_task.xml). */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        Log.v("TaskAdapter", "onCreateViewHolder called") // Verbose log for creation
        val binding = ItemTaskBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pass the click and checked-change listeners to the ViewHolder.
        return TaskViewHolder(binding, onTaskClick, onTaskCheckedChange)
    }

    /** Binds data from a TaskEntity object to the views within a given ViewHolder. */
    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val currentTask = getItem(position) // Get the task data for this position.
        Log.v("TaskAdapter", "onBindViewHolder called for position: $position, Task ID: ${currentTask.id}")
        holder.bind(currentTask) // Bind the data to the views in the holder.
    }

    /**
     * ViewHolder class for a single task item in the RecyclerView.
     * Holds references to the views (via ViewBinding) and sets up interaction listeners.
     */
    class TaskViewHolder(
        private val binding: ItemTaskBinding, // ViewBinding instance for the item_task.xml layout
        private val onTaskClick: (TaskEntity) -> Unit, // Lambda for item click
        private val onTaskCheckedChange: (TaskEntity, Boolean) -> Unit // Lambda for checkbox change
    ) : RecyclerView.ViewHolder(binding.root) { // Pass the root view (LinearLayout) to the superclass

        // Store the task currently bound to this ViewHolder to access it in listeners.
        private var currentTask: TaskEntity? = null

        init {
            Log.v("TaskViewHolder", "ViewHolder init block")
            // --- Set up listeners once when the ViewHolder is created ---

            // Listener for the CheckBox state changes.
            binding.checkBoxTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                // Only invoke the callback if a task is currently bound and
                // the checkbox state actually changed from the task's current state.
                currentTask?.let { task ->
                    if (task.isCompleted != isChecked) {
                        Log.d("TaskViewHolder", "Checkbox changed for task ID ${task.id}, new state: $isChecked")
                        onTaskCheckedChange(task, isChecked)
                    } else {
                        // This might happen if setChecked is called programmatically elsewhere
                        // without removing the listener first.
                        Log.v("TaskViewHolder", "Checkbox listener fired but state is same for task ID ${task.id}")
                    }
                } ?: Log.w("TaskViewHolder", "Checkbox changed but currentTask is null!")
            }

            // Listener for clicks on the entire item view (e.g., to edit the task).
            itemView.setOnClickListener {
                currentTask?.let { task ->
                    Log.d("TaskViewHolder", "Item clicked for task ID ${task.id}")
                    onTaskClick(task) // Execute the item click lambda.
                } ?: Log.w("TaskViewHolder", "Item clicked but currentTask is null!")
            }
        }

        /** Binds the data from a TaskEntity to the views in this ViewHolder. */
        fun bind(task: TaskEntity) {
            currentTask = task // Update the reference to the currently displayed task.
            binding.textViewTaskDescription.text = task.description

            // ** Safely set the CheckBox state without triggering the listener **
            // 1. Temporarily remove the listener to prevent infinite loops or unwanted calls.
            binding.checkBoxTaskCompleted.setOnCheckedChangeListener(null)
            // 2. Set the checkbox's checked state based on the task data.
            binding.checkBoxTaskCompleted.isChecked = task.isCompleted
            // 3. Re-attach the listener so user interactions are captured.
            binding.checkBoxTaskCompleted.setOnCheckedChangeListener { _, isChecked ->
                 currentTask?.let { t -> if(t.isCompleted != isChecked) onTaskCheckedChange(t, isChecked)}
            }

            // Apply or remove the strike-through effect on the description text.
            updateStrikeThrough(task.isCompleted)
            Log.v("TaskViewHolder", "Bound data for task ID ${task.id}, Desc: ${task.description}, Completed: ${task.isCompleted}")
        }

        /** Helper function to add or remove the strike-through paint flag on the description TextView. */
        private fun updateStrikeThrough(isCompleted: Boolean) {
             val textView = binding.textViewTaskDescription
             if (isCompleted) {
                // Add the strike-through flag to the existing flags.
                textView.paintFlags = textView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            } else {
                // Remove the strike-through flag using bitwise AND with the inverted flag.
                textView.paintFlags = textView.paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv()
            }
        }
    }

    /** Companion object holding the DiffUtil configuration for efficient list updates. */
    companion object {
        // DiffUtil callback tells the ListAdapter how to determine differences
        // between the old list and the new list.
        private val DiffCallback = object : DiffUtil.ItemCallback<TaskEntity>() {
            /**
             * Called to check whether two items represent the same logical item.
             * Typically compares unique identifiers.
             */
            override fun areItemsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
                return oldItem.id == newItem.id
            }

            /**
             * Called only if areItemsTheSame returns true. Checks if the visual representation
             * of the item has changed. Compares the contents of the items.
             */
            override fun areContentsTheSame(oldItem: TaskEntity, newItem: TaskEntity): Boolean {
                // The data class `equals` implementation automatically compares all properties.
                // This is usually sufficient unless you have complex fields affecting the UI
                // that aren't part of the data class equality check.
                return oldItem == newItem
            }
        }
    }
}
