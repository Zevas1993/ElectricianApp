package com.example.electricianapp.ui.jobs

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import com.example.electricianapp.data.local.entity.JobEntity
import com.example.electricianapp.domain.repository.jobs.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class JobListViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val savedStateHandle: SavedStateHandle // For handling navigation arguments
) : ViewModel() {

    // Get the userId passed from LoginFragment via navigation arguments
    // The key "userId" must match the argument name defined in nav_graph.xml
    val userId: Long = savedStateHandle.get<Long>("userId") ?: throw IllegalArgumentException("User ID not found in arguments") // Made public

    // LiveData holding the list of jobs for the current user
    val jobs: LiveData<List<JobEntity>> = jobRepository.getAllJobsForUser(userId)

    // TODO: Add functions for handling user actions like deleting a job, etc.
    // Example:
    // fun deleteJob(job: JobEntity) {
    //     viewModelScope.launch {
    //         jobRepository.deleteJob(job)
    //     }
    // }
}
