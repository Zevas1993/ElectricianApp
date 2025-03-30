package com.example.electricianapp.data.repository
import android.util.Log
import com.example.electricianapp.data.local.dao.JobDao
import com.example.electricianapp.data.model.JobEntity
// import com.google.firebase.firestore.FirebaseFirestore // Uncomment if syncing
// import kotlinx.coroutines.tasks.await // For await() on Firestore tasks if syncing
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of the JobRepository interface.
 * Interacts with the local JobDao (Room) and potentially Firestore for syncing.
 */
@Singleton
class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao
    // private val firestore: FirebaseFirestore // Uncomment and inject if syncing job data
) : JobRepository {

    private val TAG = "JobRepositoryImpl"

    /** Retrieves jobs for a user primarily from the local database (via Flow). */
    override fun getJobsForUser(userId: Long): Flow<List<JobEntity>> {
        Log.d(TAG, "Getting jobs flow for user ID: $userId")
        // TODO: Implement Firestore listener here if real-time sync is needed.
        //       The listener would fetch from Firestore, update the local Room DB,
        //       and Room's Flow would automatically emit the changes.
        //       For now, just return the local Flow.
        return jobDao.getJobsForUser(userId)
    }

    /** Retrieves a single job by ID primarily from the local database (via Flow). */
    override fun getJobById(jobId: Long): Flow<JobEntity?> {
        Log.d(TAG, "Getting job by ID flow: $jobId")
        // TODO: Implement Firestore listener for single document if real-time sync needed.
        return jobDao.getJobById(jobId)
    }

    /** Saves (inserts or updates) a job in the local database and optionally syncs to Firestore. */
    override suspend fun saveJob(job: JobEntity): Long {
        // Room's REPLACE strategy handles both insert (if id=0) and update (if id>0).
        Log.d(TAG, "Saving job ID: ${job.id} (0 means new), Title: ${job.title}")
        val savedOrUpdatedId = jobDao.insert(job)

        // Determine the effective ID (either the original or the newly generated one)
        val effectiveId = if (job.id != 0L) job.id else savedOrUpdatedId
        if (effectiveId == -1L) {
             Log.e(TAG, "Local job save failed (returned -1). Title: ${job.title}")
             throw Exception("Failed to save job locally.") // Throw exception to signal failure
        }
        Log.i(TAG, "Job saved locally with effective ID: $effectiveId")

        // ** Firestore Sync (Optional Example) **
        /*
        try {
            // Use the effective ID as the document ID in Firestore for consistency
            // Store under a user-specific collection path
            firestore.collection("users").document(job.userId.toString()) // Assuming userId matches authUid or another stable ID
                     .collection("jobs").document(effectiveId.toString())
                     .set(job.copy(id = 0)) // Don't store local ID in Firestore copy
                     .await()
            Log.i(TAG, "Job synced to Firestore: users/${job.userId}/jobs/$effectiveId")
        } catch (e: Exception) {
             Log.e(TAG, "Error syncing job $effectiveId to Firestore", e)
             // Handle sync error - maybe queue it? For now, local save succeeded.
        }
        */

        return effectiveId // Return the definitive ID
    }

    /** Deletes a job from the local database and optionally syncs the deletion to Firestore. */
    override suspend fun deleteJob(job: JobEntity) {
        Log.d(TAG, "Deleting job ID: ${job.id}, Title: ${job.title}")
        jobDao.delete(job) // Delete locally
        Log.i(TAG, "Job deleted locally: ID ${job.id}")

        // ** Firestore Sync (Optional Example) **
        /*
        try {
            firestore.collection("users").document(job.userId.toString())
                     .collection("jobs").document(job.id.toString())
                     .delete()
                     .await()
            Log.i(TAG, "Job deletion synced to Firestore: users/${job.userId}/jobs/${job.id}")
        } catch (e: Exception) {
             Log.e(TAG, "Error syncing job ${job.id} deletion to Firestore", e)
             // Handle sync error
        }
        */
        // Note: Associated tasks should be deleted locally by Room's CASCADE.
        // If syncing tasks, ensure they are also deleted from Firestore, either here
        // or using a Cloud Function triggered by the job deletion.
    }
}
