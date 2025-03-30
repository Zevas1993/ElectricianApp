package com.example.electricianapp.ui.jobs
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.electricianapp.R // Import R for potential resource usage later
import com.example.electricianapp.data.model.JobEntity
import com.example.electricianapp.data.model.JobStatus
import com.example.electricianapp.databinding.ItemJobBinding // Ensure this matches your generated binding class name
import java.text.SimpleDateFormat
import java.util.*
import android.util.Log // Added for logging

/**
 * RecyclerView Adapter for displaying a list of JobEntity objects using ListAdapter and DiffUtil.
 * Handles item clicks via a lambda function passed in the constructor.
 */
class JobAdapter(
    // Lambda to be executed when an item is clicked, passing the clicked JobEntity.
    private val onItemClicked: (JobEntity) -> Unit
) : ListAdapter<JobEntity, JobAdapter.JobViewHolder>(DiffCallback) { // Extends ListAdapter

    // Date formatter instance. Consider making this locale-aware or injecting it.
    // Using a companion object to avoid creating it repeatedly.
    companion object {
         private val DATE_FORMATTER = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

         // DiffUtil callback for calculating list differences efficiently.
         private val DiffCallback = object : DiffUtil.ItemCallback<JobEntity>() {
            /** Checks if two items represent the same database entity (usually based on ID). */
            override fun areItemsTheSame(oldItem: JobEntity, newItem: JobEntity): Boolean {
                return oldItem.id == newItem.id
            }

            /** Checks if the content (data displayed) of two items is the same. */
            override fun areContentsTheSame(oldItem: JobEntity, newItem: JobEntity): Boolean {
                // Data class `equals` method compares all properties. This is usually sufficient.
                return oldItem == newItem
            }
         }
    }


    /** Creates new ViewHolders by inflating the item layout. */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobViewHolder {
        // Inflate the layout using ViewBinding.
        val binding = ItemJobBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        // Pass the click listener lambda to the ViewHolder.
        return JobViewHolder(binding, onItemClicked)
    }

    /** Binds data from a JobEntity object to the views within a given ViewHolder. */
    override fun onBindViewHolder(holder: JobViewHolder, position: Int) {
        val currentJob = getItem(position) // Get the JobEntity at the current position.
        holder.bind(currentJob, DATE_FORMATTER) // Bind the data using the ViewHolder's bind method.
    }

    /**
     * ViewHolder class for a single job item. Holds references to the views
     * and sets up the click listener.
     */
    class JobViewHolder(
        private val binding: ItemJobBinding, // ViewBinding instance for item_job.xml
        private val onItemClicked: (JobEntity) -> Unit // The click listener lambda
        ) : RecyclerView.ViewHolder(binding.root) { // Pass the root view (CardView)

        private var currentJob: JobEntity? = null // Store the job bound to this holder

        init {
            // Set the click listener on the root view (the CardView) of the item layout.
            itemView.setOnClickListener {
                // Only trigger the callback if a job is actually bound to this holder.
                currentJob?.let { job ->
                    onItemClicked(job) // Execute the lambda passed from the adapter.
                }
            }
        }

        /** Binds the JobEntity data to the UI elements within the ViewHolder's layout. */
        fun bind(job: JobEntity, formatter: SimpleDateFormat) {
            currentJob = job // Keep track of the bound job
            binding.textViewJobTitle.text = job.title
            binding.textViewJobDate.text = formatter.format(Date(job.date)) // Format timestamp
            binding.textViewJobStatus.text = job.status.name // Display status enum name

            // Optional: Set status background color based on JobStatus
            val statusColorRes = when (job.status) {
                JobStatus.COMPLETED -> R.color.status_completed
                JobStatus.IN_PROGRESS -> R.color.status_inprogress
                JobStatus.CANCELLED -> R.color.status_cancelled
                JobStatus.SCHEDULED -> R.color.status_scheduled
            }
            try { // Use try-catch for context safety if needed, though binding.root should have it
                 binding.textViewJobStatus.setBackgroundColor(
                     itemView.context.getColor(statusColorRes)
                 )
                 // Adjust text color for better contrast on some backgrounds if needed
                 // val textColor = if (job.status == JobStatus.IN_PROGRESS) Color.BLACK else Color.WHITE
                 // binding.textViewJobStatus.setTextColor(textColor)
            } catch (e: Exception) {
                 // Log error if context is unavailable
                 Log.e("JobViewHolder", "Error setting status background color", e)
            }
        }
    }
}
