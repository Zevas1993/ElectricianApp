package com.example.electricianapp.domain.repository.jobs

import androidx.lifecycle.LiveData
import com.example.electricianapp.data.local.entity.JobEntity

interface JobRepository {

    suspend fun insertJob(job: JobEntity): Long

    suspend fun updateJob(job: JobEntity)

    suspend fun deleteJob(job: JobEntity)

    fun getJobById(jobId: Long): LiveData<JobEntity?>

    fun getAllJobsForUser(userId: Long): LiveData<List<JobEntity>>

    // Add other repository methods as needed
}
