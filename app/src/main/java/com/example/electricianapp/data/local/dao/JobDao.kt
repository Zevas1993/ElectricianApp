package com.example.electricianapp.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.electricianapp.data.local.entity.JobEntity

@Dao
interface JobDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertJob(job: JobEntity): Long

    @Update
    suspend fun updateJob(job: JobEntity)

    @Delete
    suspend fun deleteJob(job: JobEntity)

    @Query("SELECT * FROM jobs WHERE id = :jobId")
    fun getJobById(jobId: Long): LiveData<JobEntity?> // Use LiveData for observation

    @Query("SELECT * FROM jobs WHERE userId = :userId ORDER BY createdAt DESC")
    fun getAllJobsForUser(userId: Long): LiveData<List<JobEntity>> // Get jobs for a specific user

    // Add other queries as needed, e.g., search, filter by status
}
