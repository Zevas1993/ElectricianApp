package com.example.electricianapp.ui.jobs
import android.util.Log
import androidx.lifecycle.*
import com.example.electricianapp.data.model.JobEntity
import com.example.electricianapp.data.model.JobStatus
import com.example.electricianapp.data.repository.JobRepository
import com.example.electricianapp.ui.utils.Event // Reuse Event wrapper for single-shot events
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.firstOrNull // To get a single value from Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the AddEditJobFragment.
 * Handles loading an existing job (if editing), saving (creating or updating) a job,
 * and providing data/events back to the Fragment.
 */
@HiltViewModel
class AddEditJobViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    savedStateHandle: SavedStateHandle // Access navigation arguments passed to the fragment
) : ViewModel() {

    // Retrieve arguments passed via Safe Args from the navigation graph.
    // Throw exception if userId is missing, as it's essential for saving.
    val userId: Long = savedStateHandle["userId"]
        ?: throw IllegalStateException("User ID argument missing in AddEditJobViewModel")
    private val jobId: Long = savedStateHandle["jobId"] ?: -1L // -1L indicates adding a new job

    // Holds the job being edited, fetched only if jobId is valid. Null when adding.
    private var currentJob: JobEntity? = null

    // LiveData to expose the loaded job details to the fragment for populating fields.
    private val _job = MutableLiveData<JobEntity?>()
    val job: LiveData<JobEntity?> = _job

    // LiveData events for communicating back to the fragment.
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> = _navigateBackEvent // Signals successful save

    private val _showErrorEvent = MutableLiveData<Event<String>>()
    val showErrorEvent: LiveData<Event<String>> = _showErrorEvent // Signals an error occurred

    // Simple flag derived from jobId to easily check mode in Fragment/ViewModel.
    val isEditMode = jobId != -1L

    init {
        // If jobId is valid (not -1L), it means we are editing an existing job.
        // Launch a coroutine to fetch the job data when the ViewModel is created.
        if (isEditMode) {
            Log.d("AddEditJobViewModel", "Initializing in Edit mode for job ID: $jobId")
            viewModelScope.launch {
                // Use firstOrNull() to get the first emission (the current state) from the Flow
                // provided by the repository. This acts as a one-time fetch suitable for an edit screen.
                currentJob = jobRepository.getJobById(jobId).firstOrNull()
                _job.postValue(currentJob) // Update LiveData for the Fragment to observe and populate fields
                if (currentJob == null) {
                    // Log an error if the job ID was valid but the job couldn't be found.
                    Log.e("AddEditJobViewModel", "Error: Job with ID $jobId not found in repository for editing.")
                    // Post an error event to inform the user or trigger navigation back.
                    _showErrorEvent.postValue(Event("Error: Could not load job data."))
                    // Consider navigating back if job load fails in edit mode:
                    // _navigateBackEvent.postValue(Event(Unit))
                } else {
                    Log.d("AddEditJobViewModel", "Job loaded successfully for editing: ${currentJob?.title}")
                }
            }
        } else {
            Log.d("AddEditJobViewModel", "Initializing in Add mode for user ID: $userId")
            // No job needs to be loaded when adding a new one.
        }
    }

    /**
     * Saves the job data (either creates a new job or updates the existing one).
     * Performs basic validation on the title field.
     *
     * @param title The job title (required).
     * @param address The site address (optional).
     * @param description The job description (optional).
     * @param status The selected JobStatus.
     */
    fun saveJob(title: String, address: String?, description: String?, status: JobStatus) {
        val trimmedTitle = title.trim()
        // --- Validation ---
        if (trimmedTitle.isBlank()) {
            _showErrorEvent.value = Event("Job title cannot be empty") // Use specific error message
            return // Stop processing if validation fails
        }
        // Add more validation as needed (e.g., address format, description length)

        viewModelScope.launch {
            try {
                // --- Determine Job Entity to Save ---
                val jobToSave: JobEntity = if (isEditMode && currentJob != null) {
                    // ** Update Existing Job **
                    // Create a copy of the loaded 'currentJob' with modifications from the form fields.
                    Log.d("AddEditJobViewModel", "Updating existing job ID: ${currentJob!!.id}")
                    currentJob!!.copy(
                        title = trimmedTitle,
                        // Use takeUnless(String::isBlank) to store null if the trimmed string is empty.
                        siteAddress = address?.trim().takeUnless { it.isNullOrBlank() },
                        description = description?.trim().takeUnless { it.isNullOrBlank() },
                        status = status
                        // Important: Keep the original 'id', 'userId', and 'date' from 'currentJob' when updating.
                    )
                } else {
                     // ** Create New Job **
                     // Construct a new JobEntity instance.
                     Log.d("AddEditJobViewModel", "Creating new job for user ID: $userId")
                     JobEntity(
                         // Let Room auto-generate the 'id' (by leaving it as default 0).
                         userId = userId, // Associate with the correct user.
                         title = trimmedTitle,
                         siteAddress = address?.trim().takeUnless { it.isNullOrBlank() },
                         description = description?.trim().takeUnless { it.isNullOrBlank() },
                         status = status,
                         date = System.currentTimeMillis() // Set the creation/scheduling timestamp for new jobs.
                     )
                }

                // --- Save to Repository ---
                Log.d("AddEditJobViewModel", "Calling jobRepository.saveJob for: ${jobToSave.title}")
                jobRepository.saveJob(jobToSave) // Perform the database operation.
                Log.i("AddEditJobViewModel", "Job save successful.")

                // --- Signal Success ---
                _navigateBackEvent.value = Event(Unit) // Trigger navigation back in the fragment.

            } catch (e: Exception) {
                // --- Handle Errors ---
                Log.e("AddEditJobViewModel", "Error occurred during saveJob", e)
                // Post an error event to be shown to the user.
                _showErrorEvent.value = Event("Error saving job. Please try again.")
            }
        }
    }
}
