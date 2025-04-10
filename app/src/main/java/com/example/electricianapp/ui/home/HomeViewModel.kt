package com.example.electricianapp.ui.home

import androidx.lifecycle.*
// import androidx.lifecycle.Transformations // Removing this as it causes issues
import com.example.electricianapp.R
import com.example.electricianapp.domain.repository.jobs.JobRepository
import dagger.hilt.android.lifecycle.HiltViewModel
// Removed Flow imports as we are using LiveData now
import javax.inject.Inject

// Data class for Job Summary
data class JobSummary(
    val activeJobs: Int = 0,
    val totalJobs: Int = 0
)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val jobRepository: JobRepository
) : ViewModel() {

    // --- Job Summary ---
    // TODO: Implement actual logic in JobRepository to get active vs total counts
    // TODO: Need a way to get the current userId (e.g., from SavedStateHandle or another source)
    private val currentUserId = 0L // Placeholder user ID

    // Manual implementation instead of Transformations.map
    private val _jobSummary = MediatorLiveData<JobSummary>()
    val jobSummary: LiveData<JobSummary> = _jobSummary

    private val allJobsLiveData = jobRepository.getAllJobsForUser(currentUserId)

    // --- Quick Access Items ---
    private val _quickAccessItems = MutableLiveData<List<QuickAccessItem>>()
    val quickAccessItems: LiveData<List<QuickAccessItem>> = _quickAccessItems

    init {
        // Observe the source LiveData to manually update _jobSummary
        _jobSummary.addSource(allJobsLiveData) { jobList ->
            // Placeholder logic: Assume all jobs are active for now
            _jobSummary.value = JobSummary(activeJobs = jobList.size, totalJobs = jobList.size)
        }
        loadQuickAccessItems()
    }

    private fun loadQuickAccessItems() {
        // Define the quick access items
        // TODO: Replace placeholder icons with actual drawables
        val items = listOf(
            QuickAccessItem(
                id = "jobs",
                label = "Jobs",
                iconResId = R.drawable.ic_job,
                navigationActionId = R.id.action_homeFragment_to_jobListFragment
            ),
            QuickAccessItem(
                id = "add_job",
                label = "Add Job",
                iconResId = R.drawable.ic_add_job,
                navigationActionId = R.id.action_homeFragment_to_addEditJobFragment
                // TODO: Need to pass arguments for Add Job (userId, jobId=-1)
            ),
            QuickAccessItem(
                id = "calculators",
                label = "Calculators",
                iconResId = R.drawable.ic_calculator,
                navigationActionId = R.id.action_homeFragment_to_calculatorListFragment
            ),
            QuickAccessItem(
                id = "inventory",
                label = "Material Inventory",
                iconResId = R.drawable.ic_inventory,
                navigationActionId = R.id.materialInventoryFragment
            )
            // Add future items like "Tools", "Settings" here
        )
        _quickAccessItems.value = items
    }
}
