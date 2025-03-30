package com.example.electricianapp.data.repository
import android.util.Log
import com.example.electricianapp.data.local.dao.TaskDao
import com.example.electricianapp.data.model.TaskEntity
// import com.google.firebase.firestore.FirebaseFirestore // Uncomment if syncing
// import kotlinx.coroutines.tasks.await // For await() on Firestore tasks if syncing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of the TaskRepository interface.
 * Interacts with the local TaskDao (Room) and potentially Firestore.
 */
@Singleton
class TaskRepositoryImpl @Inject constructor(
    private val taskDao: TaskDao
    // private val firestore: FirebaseFirestore // Uncomment and inject if syncing task data
) : TaskRepository {

    private val TAG = "TaskRepositoryImpl"

    /** Retrieves tasks for a job primarily from the local database (via Flow). */
    override fun getTasksForJob(jobId: Long): Flow<List<TaskEntity>> {
        Log.d(TAG, "Getting tasks flow for job ID: $jobId")
        // TODO: Implement Firestore listener for task subcollection if real-time sync is needed.
        return taskDao.getTasksForJob(jobId)
    }

    /** Retrieves a single task by ID primarily from the local database. */
    override suspend fun getTaskById(taskId: Long): TaskEntity? {
        Log.d(TAG, "Getting task by ID: $taskId")
        // TODO: Implement Firestore fetch logic if necessary (e.g., check cache first).
        return taskDao.getTaskById(taskId)
    }

    /** Saves (inserts or updates) a task in the local database and optionally syncs to Firestore. */
    override suspend fun saveTask(task: TaskEntity): Long {
        Log.d(TAG, "Saving task ID: ${task.id} (0 means new) for job ID: ${task.jobId}")
        // Room's REPLACE strategy handles insert/update.
        val savedOrUpdatedId = taskDao.insert(task)

        val effectiveId = if (task.id != 0L) task.id else savedOrUpdatedId
        if (effectiveId == -1L) {
            Log.e(TAG, "Local task save failed (returned -1). JobID: ${task.jobId}, Desc: ${task.description}")
            throw Exception("Failed to save task locally.")
        }
        Log.i(TAG, "Task saved locally with effective ID: $effectiveId")

        // ** Firestore Sync (Optional Example) **
        /*
        try {
            // Store tasks in a subcollection under the job document
            // Assumes you have the user ID available or can retrieve it if needed for the path
            // String userId = getUserIdForJob(task.jobId) // You'd need a way to get this if not implicit
            firestore.collection("users").document(userId) // Replace with actual user path segment
                     .collection("jobs").document(task.jobId.toString())
                     .collection("tasks").document(effectiveId.toString())
                     .set(task.copy(id = 0)) // Don't store local ID
                     .await()
            Log.i(TAG, "Task synced to Firestore: .../jobs/${task.jobId}/tasks/$effectiveId")
        } catch (e: Exception) {
             Log.e(TAG, "Error syncing task $effectiveId to Firestore", e)
             // Handle sync error
        }
        */
        return effectiveId
    }

    /** Deletes a task from the local database and optionally syncs the deletion to Firestore. */
    override suspend fun deleteTask(task: TaskEntity) {
        Log.d(TAG, "Deleting task ID: ${task.id} from job ID: ${task.jobId}")
        taskDao.deleteTask(task) // Delete locally
        Log.i(TAG, "Task deleted locally: ID ${task.id}")

        // ** Firestore Sync (Optional Example) **
        /*
        try {
             // String userId = getUserIdForJob(task.jobId) // Get user path segment
             firestore.collection("users").document(userId)
                      .collection("jobs").document(task.jobId.toString())
                      .collection("tasks").document(task.id.toString())
                      .delete()
                      .await()
            Log.i(TAG, "Task deletion synced to Firestore: .../jobs/${task.jobId}/tasks/${task.id}")
        } catch (e: Exception) {
             Log.e(TAG, "Error syncing task ${task.id} deletion to Firestore", e)
             // Handle sync error
        }
        */
    }
}
