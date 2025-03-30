package com.example.electricianapp.data.local.dao
import androidx.room.*
import com.example.electricianapp.data.model.JobEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for Job operations.
 * Defines methods for interacting with the 'jobs' table using Room annotations.
 * Using Flow for read operations provides observable data streams that automatically
 * update the UI when the underlying data changes.
 */
@Dao
interface JobDao {
    /**
     * Inserts a job into the database.
     * `OnConflictStrategy.REPLACE`: If a job with the same primary key (`id`)
     * already exists, the existing job will be replaced with the new one.
     * This strategy effectively handles both insert and update operations if the
     * object passed always contains the correct ID (0 for new, >0 for existing).
     *
     * @param job The JobEntity object to insert or replace.
     * @return The SQLite row ID of the inserted/replaced job.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(job: JobEntity): Long

    /**
     * Updates an existing job in the database.
     * Room identifies the job to update based on the primary key (`id`) of the passed object.
     * If OnConflictStrategy.REPLACE is used in insert, this @Update might be redundant
     * unless specific update logic (not replacing all fields) is needed later.
     *
     * @param job The JobEntity object with updated information.
     */
    @Update
    suspend fun update(job: JobEntity)

    /**
     * Deletes a specific job from the database.
     * Room identifies the job to delete based on its primary key.
     *
     * @param job The JobEntity object to delete.
     */
    @Delete
    suspend fun delete(job: JobEntity)

    /**
     * Retrieves an observable Flow containing a list of all jobs associated with a specific user ID.
     * The list is ordered by the job's date in descending order (most recent jobs first).
     * The Flow will automatically emit a new list whenever the jobs for this user change in the database.
     *
     * @param userId The ID of the user whose jobs are to be retrieved.
     * @return A Flow emitting List<JobEntity>.
     */
    @Query("SELECT * FROM jobs WHERE userId = :userId ORDER BY date DESC")
    fun getJobsForUser(userId: Long): Flow<List<JobEntity>>

    /**
     * Retrieves an observable Flow of a single job based on its unique ID.
     * The Flow will emit the JobEntity object if found.
     * If the job is deleted later, the Flow will emit null.
     *
     * @param jobId The ID of the job to retrieve.
     * @return A Flow emitting JobEntity? (nullable JobEntity).
     */
    @Query("SELECT * FROM jobs WHERE id = :jobId LIMIT 1")
    fun getJobById(jobId: Long): Flow<JobEntity?>
}
