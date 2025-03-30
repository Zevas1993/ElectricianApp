package com.example.electricianapp.ui.jobs
import android.util.Log
import androidx.lifecycle.*
import com.example.electricianapp.data.model.JobEntity
import com.example.electricianapp.data.model.TaskEntity
import com.example.electricianapp.data.repository.JobRepository
import com.example.electricianapp.data.repository.TaskRepository
import com.example.electricianapp.ui.utils.Event // Reusable Event wrapper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the JobDetailFragment.
 * Manages fetching job details, associated tasks, and handling actions like
 * updating task status, deleting tasks, or deleting the job itself.
 */
@HiltViewModel
class JobDetailViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val taskRepository: TaskRepository,
    savedStateHandle: SavedStateHandle // Used to access navigation arguments
) : ViewModel() {

    // Retrieve the jobId passed from JobListFragment via navigation arguments.
    // Throw exception if ID is missing, as it's required for this screen.
    val jobId: Long = savedStateHandle["jobId"]
        ?: throw IllegalStateException("Job ID required but not found in arguments for JobDetailViewModel")

    init {
        Log.d("JobDetailViewModel", "ViewModel initialized for job ID: $jobId")
    }

    // Expose the Job details as observable LiveData. Updates automatically if data changes.
    // Include error handling for the Flow.
    val job: LiveData<JobEntity?> = jobRepository.getJobById(jobId)
        .catch { exception ->
             Log.e("JobDetailViewModel", "Error fetching job $jobId", exception)
             _showErrorEvent.postValue(Event("Failed to load job details."))
             emit(null) // Emit null on error
        }
        .asLiveData()

    // Expose the list of Tasks for this job as observable LiveData.
    // Include error handling for the Flow.
    val tasks: LiveData<List<TaskEntity>> = taskRepository.getTasksForJob(jobId)
         .catch { exception ->
             Log.e("JobDetailViewModel", "Error fetching tasks for job $jobId", exception)
             _showErrorEvent.postValue(Event("Failed to load tasks."))
             emit(emptyList()) // Emit empty list on error
         }
        .asLiveData()

    // --- Events for communication with the Fragment ---
    private val _navigateBackEvent = MutableLiveData<Event<Unit>>()
    val navigateBackEvent: LiveData<Event<Unit>> = _navigateBackEvent // Event after deletion

    private val _showErrorEvent = MutableLiveData<Event<String>>()
    val showErrorEvent: LiveData<Event<String>> = _showErrorEvent // Event for critical errors

    private val _showSnackbarEvent = MutableLiveData<Event<String>>()
    val showSnackbarEvent: LiveData<Event<String>> = _showSnackbarEvent // Event for info/undo messages
    // --- End Events ---


    /** Updates the completion status of a task in the database. */
    fun updateTaskStatus(task: TaskEntity, isCompleted: Boolean) {
        // Only proceed if the status is actually different
        if (task.isCompleted != isCompleted) {
            Log.d("JobDetailViewModel", "Updating task ${task.id} status to $isCompleted")
            viewModelScope.launch {
                 try {
                    // Create a copy with the new status and save via repository
                    taskRepository.saveTask(task.copy(isCompleted = isCompleted))
                 } catch (e: Exception) {
                     Log.e("JobDetailViewModel", "Failed to update task ${task.id} status", e)
                     _showErrorEvent.postValue(Event("Failed to update task status"))
                 }
            }
        }
    }

    /** Deletes the current job from the database via the repository. */
    fun deleteJob() {
         viewModelScope.launch {
             val currentJob = job.value // Get the current value held by LiveData
             if (currentJob != null) {
                 Log.d("JobDetailViewModel", "Attempting to delete job ID: ${currentJob.id}")
                 try {
                     jobRepository.deleteJob(currentJob)
                     // Note: Tasks associated with this job should be automatically deleted
                     // by the database engine due to the CASCADE constraint set on the TaskEntity.
                     Log.i("JobDetailViewModel", "Job ${currentJob.id} delete initiated.")
                     _navigateBackEvent.postValue(Event(Unit)) // Signal success to fragment for navigation
                 } catch (e: Exception) {
                     Log.e("JobDetailViewModel", "Failed to delete job ${currentJob.id}", e)
                     _showErrorEvent.postValue(Event("Failed to delete job"))
                 }
             } else {
                 // This case should be rare if the delete option is only shown when job is loaded.
                 Log.w("JobDetailViewModel", "Delete called but job LiveData is null.")
                 _showErrorEvent.postValue(Event("Cannot delete, job data not available."))
             }
         }
    }

     /** Deletes a specific task (e.g., when swiped in the UI). */
     fun deleteTask(task: TaskEntity) {
         Log.d("JobDetailViewModel", "Attempting to delete task ID: ${task.id}")
         viewModelScope.launch {
             try {
                taskRepository.deleteTask(task)
                Log.i("JobDetailViewModel", "Task ${task.id} delete initiated.")
                // Optionally confirm deletion with a Snackbar message via event
                // _showSnackbarEvent.postValue(Event("Task deleted"))
             } catch(e: Exception) {
                 Log.e("JobDetailViewModel", "Failed to delete task ${task.id}", e)
                 _showErrorEvent.postValue(Event("Failed to delete task"))
             }
         }
     }

     /** Re-saves a task, usually called when an "Undo" action is triggered after deletion. */
      fun saveTaskForUndo(task: TaskEntity) {
         Log.d("JobDetailViewModel", "Attempting to re-save (undo delete) task ID: ${task.id}")
         viewModelScope.launch {
             try {
                 // Call saveTask - repository implementation handles insert/update.
                 taskRepository.saveTask(task)
                 Log.i("JobDetailViewModel", "Task ${task.id} re-saved successfully (Undo).")
             } catch (e: Exception) {
                 Log.e("JobDetailViewModel", "Failed to re-save task ${task.id} (Undo)", e)
                 _showErrorEvent.postValue(Event("Failed to undo task deletion"))
             }
         }
      }
}
