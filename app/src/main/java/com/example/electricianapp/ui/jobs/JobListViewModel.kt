package com.example.electricianapp.ui.jobs
import android.util.Log
import androidx.lifecycle.*
import com.example.electricianapp.data.model.JobEntity
import com.example.electricianapp.data.repository.JobRepository
import com.example.electricianapp.data.repository.UserRepository // Optional: For user details
import com.google.firebase.auth.FirebaseAuth // For sign out action
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch // For handling errors in the Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the JobListFragment.
 * Responsible for fetching and exposing the list of jobs for the logged-in user,
 * providing user information (optional), and handling the sign-out action.
 */
@HiltViewModel
class JobListViewModel @Inject constructor(
    private val jobRepository: JobRepository,
    private val userRepository: UserRepository, // Inject if user details needed
    private val firebaseAuth: FirebaseAuth,    // Inject for sign-out logic
    savedStateHandle: SavedStateHandle // Access navigation arguments passed via Safe Args
) : ViewModel() {

    // Retrieve the userId passed from LoginFragment. Throw if missing.
    val userId: Long = savedStateHandle["userId"]
        ?: throw IllegalStateException("User ID required but not found in arguments for JobListViewModel")

    init {
        Log.d("JobListViewModel", "ViewModel initialized for user ID: $userId")
    }

    // Expose the list of jobs for the current user as LiveData.
    // Use .catch to handle potential errors during Flow collection from the repository/database.
    // .asLiveData() converts the Flow into LiveData, automatically managing coroutine scope.
    val jobs: LiveData<List<JobEntity>> = jobRepository.getJobsForUser(userId)
        .catch { exception ->
            // Log the error from the Flow pipeline
            Log.e("JobListViewModel", "Error fetching jobs for user $userId", exception)
            // Optionally emit an empty list or post an error state via another LiveData
            emit(emptyList()) // Emit empty list on error to clear UI
            // _showErrorEvent.postValue(Event("Failed to load jobs")) // Example error event
        }
        .asLiveData() // Convert Flow to LiveData

    // Example: Expose user's email as LiveData. Uses liveData builder for a one-shot suspend call.
    val userEmail: LiveData<String?> = liveData {
        val email = try {
            userRepository.getUserById(userId)?.email
        } catch (e: Exception) {
            Log.e("JobListViewModel", "Error fetching user email for ID $userId", e)
            null // Emit null on error
        }
        emit(email)
    }

    /** Initiates the sign-out process by signing out from Firebase Authentication. */
    fun signOut() {
        Log.d("JobListViewModel", "signOut called")
        firebaseAuth.signOut()
        // The Fragment handles signing out the GoogleSignInClient and navigating back to Login.
    }

    // TODO: Add LiveData for loading state (_isLoading) and possibly error events (_showErrorEvent)
    //       if job fetching becomes asynchronous or involves network calls later.
}
