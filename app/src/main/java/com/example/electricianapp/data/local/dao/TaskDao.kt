package com.example.electricianapp.data.local.dao
import androidx.room.*
import com.example.electricianapp.data.model.TaskEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Task operations.
 * Defines methods for interacting with the 'tasks' table using Room annotations.
 */
@Dao
interface TaskDao {
    /**
     * Inserts a task into the database.
     * `OnConflictStrategy.REPLACE`: If a task with the same primary key (`id`) exists, it's replaced.
     * Useful for both creating new tasks and updating existing ones.
     *
     * @param task The TaskEntity object to insert or replace.
     * @return The SQLite row ID of the inserted/replaced task.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: TaskEntity): Long

    /**
     * Updates an existing task in the database.
     * Identified by the primary key (`id`) of the passed task object.
     * May be redundant if using OnConflictStrategy.REPLACE in insert.
     *
     * @param task The TaskEntity object with updated information.
     */
    @Update
    suspend fun update(task: TaskEntity)

    /**
     * Deletes a specific task from the database.
     * Identified by the primary key of the passed task object.
     *
     * @param task The TaskEntity object to delete.
     */
    @Delete
    suspend fun deleteTask(task: TaskEntity) // Renamed from 'delete' for clarity if needed

    /**
     * Retrieves an observable Flow containing a list of all tasks associated with a specific job ID.
     * The list is ordered by the task's ID, typically reflecting insertion order.
     * The Flow automatically emits updates when tasks for this job change.
     *
     * @param jobId The ID of the job whose tasks are to be retrieved.
     * @return A Flow emitting List<TaskEntity>.
     */
    @Query("SELECT * FROM tasks WHERE jobId = :jobId ORDER BY id ASC")
    fun getTasksForJob(jobId: Long): Flow<List<TaskEntity>>

    /**
     * Retrieves a single task by its unique ID (one-shot query).
     * Returns null if no task with the given ID is found.
     * Marked as 'suspend' as it's not an observable Flow.
     *
     * @param taskId The ID of the task to retrieve.
     * @return The matching TaskEntity object, or null if not found.
     */
    @Query("SELECT * FROM tasks WHERE id = :taskId LIMIT 1")
    suspend fun getTaskById(taskId: Long): TaskEntity?

    /**
     * Deletes all tasks associated with a specific job ID.
     * This is useful if the CASCADE delete operation defined in the foreign key fails
     * or if you need to manually clear tasks before deleting a job in some scenarios.
     * Marked as 'suspend' for background execution.
     *
     * @param jobId The ID of the job whose tasks should be deleted.
     */
    @Query("DELETE FROM tasks WHERE jobId = :jobId")
    suspend fun deleteTasksForJob(jobId: Long)
}
