package com.example.electricianapp.data.repository
import com.example.electricianapp.data.model.JobEntity
import kotlinx.coroutines.flow.Flow

/**
 * Interface defining the contract for accessing and manipulating job data.
 * Abstracts the data source(s) from the ViewModels.
 */
interface JobRepository {
    /**
     * Gets an observable Flow of all jobs for a specific user, ordered by date descending.
     * @param userId The ID of the user whose jobs to fetch.
     * @return A Flow emitting the list of JobEntity objects.
     */
    fun getJobsForUser(userId: Long): Flow<List<JobEntity>>

    /**
     * Gets an observable Flow of a single job by its unique ID.
     * @param jobId The ID of the job to fetch.
     * @return A Flow emitting the JobEntity if found, or null otherwise.
     */
    fun getJobById(jobId: Long): Flow<JobEntity?>

    /**
     * Saves a job to the data source. This handles both creating new jobs
     * (if job.id is 0) and updating existing ones (if job.id is > 0).
     * @param job The JobEntity object to save.
     * @return The local database ID of the saved job.
     */
    suspend fun saveJob(job: JobEntity): Long

    /**
     * Deletes a specific job from the data source.
     * @param job The JobEntity object to delete.
     */
    suspend fun deleteJob(job: JobEntity)
}
