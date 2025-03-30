package com.example.electricianapp.ui.tasks
import android.util.Log
import androidx.lifecycle.*
import com.example.electricianapp.data.model.TaskEntity
import com.example.electricianapp.data.repository.TaskRepository
import com.example.electricianapp.ui.utils.Event // Reuse Event wrapper for single-shot events
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the AddEditTaskFragment.
 * Handles loading an existing task (if editing), saving (creating or updating) a task,
 * ensuring it's linked to the correct job, and managing communication with the Fragment.
 */
@HiltViewModel
class AddEditTaskViewModel @Inject constructor(
    private val taskRepository: TaskRepository,
    private val savedStateHandle: SavedStateHandle // Access navigation arguments
) : ViewModel() {

    // Retrieve arguments passed via Safe Args. Throw exceptions if required args are missing.
    val jobId: Long = savedStateHandle["jobId"]
        ?: throw IllegalStateException("Job ID argument missing in AddEditTaskViewModel")
    private val taskId: Long = savedStateHandle["taskId"] ?: -1L // -1L indicates adding a new task

    // Holds the task being edited, loaded only if taskId is valid.
    private var currentTask: TaskEntity? = null

    // LiveData to expose the loaded task details to the fragment for populating fields.
    private val _task = MutableLiveData<TaskEntity?>()
    val task: LiveData<TaskEntity?> = _task

    // LiveData events for Fragment communication.
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> = _navigateBackEvent // Event for successful save

    private val _showErrorEvent = MutableLiveData<Event<String>>()
    val showErrorEvent: LiveData<Event<String>> = _showErrorEvent // Event for showing errors

    // Flag to easily check if in edit mode based on the passed taskId.
    val isEditMode = taskId != -1L

    init {
        // If taskId is valid (editing an existing task), fetch its data.
        if (isEditMode) {
            Log.d("AddEditTaskViewModel", "Initializing in Edit mode for task ID: $taskId (Job ID: $jobId)")
            viewModelScope.launch {
                // Use the repository's suspend function to get the task data once.
                currentTask = taskRepository.getTaskById(taskId)
                _task.postValue(currentTask) // Update LiveData for the Fragment to observe.
                if (currentTask == null) {
                    Log.e("AddEditTaskViewModel", "Error: Task with ID $taskId not found for editing.")
                    _showErrorEvent.postValue(Event("Error: Could not load task data."))
                    // Consider navigating back if task loading fails in edit mode:
                    // _navigateBackEvent.postValue(Event(Unit))
                } else {
                     Log.d("AddEditTaskViewModel", "Task loaded successfully for editing: ${currentTask?.description}")
                }
            }
        } else {
            Log.d("AddEditTaskViewModel", "Initializing in Add mode for job ID: $jobId")
            // No task needs to be loaded when adding a new one.
        }
    }

    /**
     * Saves the task data (creates a new task or updates an existing one).
     * Performs validation on the description field.
     *
     * @param description The task description (required).
     * @param isCompleted The completion status of the task.
     */
    fun saveTask(description: String, isCompleted: Boolean) {
        val trimmedDescription = description.trim()
        // --- Validation ---
        if (trimmedDescription.isBlank()) {
            _showErrorEvent.value = Event("Task description cannot be empty") // Specific error message
            return // Stop if validation fails
        }

        viewModelScope.launch {
            try {
                // --- Determine Task Entity to Save ---
                val taskToSave: TaskEntity = if (isEditMode && currentTask != null) {
                    // ** Update Existing Task **
                    // Create a copy of the loaded 'currentTask' with updated values.
                     Log.d("AddEditTaskViewModel", "Updating existing task ID: ${currentTask!!.id}")
                    currentTask!!.copy(
                        description = trimmedDescription,
                        isCompleted = isCompleted
                        // Keep the original 'id' and 'jobId' from 'currentTask'.
                    )
                } else {
                    // ** Create New Task **
                    // Construct a new TaskEntity instance.
                    Log.d("AddEditTaskViewModel", "Creating new task for job ID: $jobId")
                    TaskEntity(
                        // Let Room auto-generate the 'id'.
                        jobId = jobId, // Associate with the correct job ID passed via arguments.
                        description = trimmedDescription,
                        isCompleted = isCompleted
                    )
                }

                // --- Save to Repository ---
                Log.d("AddEditTaskViewModel", "Calling taskRepository.saveTask for: ${taskToSave.description}")
                taskRepository.saveTask(taskToSave) // Perform the database insert or update.
                Log.i("AddEditTaskViewModel", "Task save successful.")

                // --- Signal Success ---
                _navigateBackEvent.value = Event(Unit) // Trigger navigation back in the fragment.

            } catch (e: Exception) {
                // --- Handle Errors ---
                Log.e("AddEditTaskViewModel", "Error occurred during saveTask", e)
                // Post an error event to be shown to the user.
                _showErrorEvent.value = Event("Error saving task. Please try again.")
            }
        }
    }
}
