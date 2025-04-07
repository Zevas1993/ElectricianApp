package com.example.electricianapp.data.repository.jobs

import androidx.lifecycle.LiveData
import com.example.electricianapp.data.local.dao.JobDao
import com.example.electricianapp.data.local.entity.JobEntity
import com.example.electricianapp.domain.repository.jobs.JobRepository
import javax.inject.Inject

class JobRepositoryImpl @Inject constructor(
    private val jobDao: JobDao
) : JobRepository {

    override suspend fun insertJob(job: JobEntity): Long {
        return jobDao.insertJob(job)
    }

    override suspend fun updateJob(job: JobEntity) {
        jobDao.updateJob(job)
    }

    override suspend fun deleteJob(job: JobEntity) {
        jobDao.deleteJob(job)
    }

    override fun getJobById(jobId: Long): LiveData<JobEntity?> {
        return jobDao.getJobById(jobId)
    }

    override fun getAllJobsForUser(userId: Long): LiveData<List<JobEntity>> {
        // Corrected method name from getJobsForUser to getAllJobsForUser
        return jobDao.getAllJobsForUser(userId)
    }

    // Note: The other errors (insert, delete, type mismatch) seem to stem from the deleted
    // conflicting file. They should be resolved now that the old file is gone and the
    // correct DAO methods are being called by the implementation in this file.
    // The type mismatch error specifically mentioned Flow vs LiveData, which suggests
    // the old repository might have been using Flow while the new DAO uses LiveData.
}
