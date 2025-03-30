package com.example.electricianapp.data.repository
import com.example.electricianapp.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for accessing and manipulating task data.
 */
interface TaskRepository {
    /**
     * Gets an observable Flow of all tasks associated with a specific job ID.
     * @param jobId The ID of the parent job.
     * @return A Flow emitting the list of TaskEntity objects.
     */
    fun getTasksForJob(jobId: Long): Flow<List<TaskEntity>>

    /**
     * Gets a single task by its unique ID (one-shot operation).
     * @param taskId The ID of the task to retrieve.
     * @return The TaskEntity if found, or null otherwise.
     */
    suspend fun getTaskById(taskId: Long): TaskEntity?

    /**
     * Saves a task (inserts if new, updates if existing based on ID).
     * @param task The TaskEntity object to save.
     * @return The local database ID of the saved task.
     */
    suspend fun saveTask(task: TaskEntity): Long

    /**
     * Deletes a specific task from the data source.
     * @param task The TaskEntity object to delete.
     */
    suspend fun deleteTask(task: TaskEntity)

    // Optional: Add bulk operations if needed
    // suspend fun deleteTasksForJob(jobId: Long)
}
