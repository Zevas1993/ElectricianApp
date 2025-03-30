package com.example.electricianapp.ui.auth
import android.util.Log
import androidx.lifecycle.*
import com.example.electricianapp.data.model.UserEntity
import com.example.electricianapp.data.repository.UserRepository
import com.example.electricianapp.ui.utils.Event // Import Event wrapper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/** Sealed class representing the possible outcomes of a login attempt or initial check. */
sealed class LoginResult {
    /** Indicates successful login, containing the local UserEntity. */
    data class Success(val user: UserEntity) : LoginResult()
    /** Indicates a login failure with an error message for the user. */
    data class Error(val message: String) : LoginResult()
    /** Indicates that a login process is currently in progress. */
    object Loading : LoginResult()
}

/**
 * ViewModel for the LoginFragment.
 * Handles the business logic for user authentication (traditional and Google Sign-In),
 * interacts with the UserRepository and FirebaseAuth, and exposes the login state
 * via LiveData (wrapped in Event to handle single-shot UI actions).
 */
@HiltViewModel // Marks ViewModel for Hilt injection
class LoginViewModel @Inject constructor(
    private val userRepository: UserRepository, // Injected repository for local user data access
    private val firebaseAuth: FirebaseAuth      // Injected Firebase Authentication instance
) : ViewModel() {

    // LiveData holding the current login state, wrapped in an Event
    private val _loginResult = MutableLiveData<Event<LoginResult>>()
    val loginResult: LiveData<Event<LoginResult>> = _loginResult // Expose read-only LiveData

    // Check Firebase Auth state when the ViewModel is created.
    // This handles cases where the user is already signed in when the app starts.
    init {
        checkCurrentUser()
    }

    /**
     * Checks the current Firebase Authentication state. If a user is signed in,
     * triggers the process to find or create the corresponding local user entry.
     */
    private fun checkCurrentUser() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            Log.d("LoginViewModel", "Firebase user found on ViewModel init: ${firebaseUser.uid}")
            // A Firebase user session exists. Post Loading state while we check/sync local DB.
            _loginResult.value = Event(LoginResult.Loading)
            // Proceed to ensure local user record exists and is linked.
            handleFirebaseUser(firebaseUser)
        } else {
            Log.d("LoginViewModel", "No Firebase user found on ViewModel init.")
            // No active Firebase session, wait for user to initiate login.
        }
    }

    // --- Traditional Login (Placeholder - Requires Secure Implementation) ---
    /**
     * Placeholder function for traditional username/password login.
     * **WARNING:** This implementation is insecure and MUST be replaced with
     * proper password hashing and verification against the stored hash.
     */
    fun loginUser(usernameOrEmail: String, password: String) {
        _loginResult.value = Event(LoginResult.Loading)
        viewModelScope.launch {
            try {
                // ** SECURITY WARNING: Replace this block with secure password verification **
                val user = userRepository.getUserByUsername(usernameOrEmail) ?: userRepository.getUserByEmail(usernameOrEmail)
                // Example: val passwordMatches = user != null && PasswordHasher.verify(password, user.passwordHash)
                val passwordMatches = user != null // INSECURE PLACEHOLDER

                if (passwordMatches) {
                    // TODO: If using Firebase email/pass auth, also call firebaseAuth.signInWithEmailAndPassword here.
                    Log.i("LoginViewModel", "Traditional login successful (placeholder check) for user: ${user?.email}")
                    _loginResult.postValue(Event(LoginResult.Success(user!!))) // User is non-null if check passed
                } else {
                    Log.w("LoginViewModel", "Traditional login failed for: $usernameOrEmail")
                    _loginResult.postValue(Event(LoginResult.Error("Invalid username or password")))
                }
            } catch (e: Exception) {
                Log.e("LoginViewModel", "Exception during traditional login", e)
                _loginResult.postValue(Event(LoginResult.Error("Login failed. Please try again.")))
            }
        }
    }

    // --- Google Sign-In ---
    /**
     * Initiates Firebase sign-in using a Google ID token obtained from the Google Sign-In client.
     * @param idToken The ID token provided by Google Sign-In upon successful authentication.
     */
    fun signInWithGoogleCredential(idToken: String) {
         _loginResult.value = Event(LoginResult.Loading)
         // Create a Firebase credential using the Google ID token.
         val credential = GoogleAuthProvider.getCredential(idToken, null)
         // Attempt to sign in to Firebase with the Google credential.
         firebaseAuth.signInWithCredential(credential)
             .addOnCompleteListener { task ->
                 if (task.isSuccessful) {
                     // Firebase sign-in successful. Get the FirebaseUser object.
                     Log.d("LoginViewModel", "Firebase signInWithCredential successful.")
                     task.result?.user?.let { firebaseUser ->
                         // Proceed to handle the local database aspect.
                         handleFirebaseUser(firebaseUser)
                     } ?: run {
                          // Should not happen if task is successful, but handle defensively.
                          Log.e("LoginViewModel", "Firebase signInWithCredential successful but user is null.")
                          _loginResult.postValue(Event(LoginResult.Error("Sign-In failed: Could not retrieve user details.")))
                     }
                 } else {
                     // Firebase sign-in failed.
                     Log.w("LoginViewModel", "Firebase signInWithCredential failed.", task.exception)
                     _loginResult.postValue(Event(LoginResult.Error("Firebase authentication failed. Please try again.")))
                 }
             }
    }

     /**
      * Core logic after obtaining a FirebaseUser (either from initial check or successful sign-in).
      * Checks if a corresponding user exists in the local Room database.
      * Creates a local user if one doesn't exist.
      * Links Firebase UID if an existing local user (found by email) is missing it.
      * Posts the final Success or Error state to _loginResult.
      *
      * @param firebaseUser The authenticated FirebaseUser object.
      */
     private fun handleFirebaseUser(firebaseUser: FirebaseUser) {
        viewModelScope.launch {
            try {
                // 1. Prioritize finding the local user by the unique Firebase UID.
                var localUser = userRepository.getUserByAuthUid(firebaseUser.uid)
                Log.d("LoginViewModel", "Checking local DB for authUid: ${firebaseUser.uid}. Found: ${localUser != null}")

                // 2. If not found by UID, and email is available, try finding by email.
                // This handles cases where the user might have previously registered locally
                // or used a different sign-in method linked to the same email.
                if (localUser == null && firebaseUser.email != null) {
                    Log.d("LoginViewModel", "User not found by authUid, checking by email: ${firebaseUser.email}")
                    localUser = userRepository.getUserByEmail(firebaseUser.email!!)
                    Log.d("LoginViewModel", "Found by email: ${localUser != null}")
                }

                if (localUser == null) {
                    // 3. Local user truly doesn't exist. Create a new UserEntity.
                    Log.i("LoginViewModel", "Creating new local user for authUid: ${firebaseUser.uid}, email: ${firebaseUser.email}")
                    val newUser = UserEntity(
                        authUid = firebaseUser.uid,
                        // Ensure email is non-null and unique. Provide a placeholder if absolutely necessary.
                        email = firebaseUser.email ?: "generated_${firebaseUser.uid}@example.com",
                        username = firebaseUser.displayName, // Use name from Google profile
                        passwordHash = null // No password for Google sign-in
                    )
                    val insertedId = userRepository.insertUser(newUser) // Insert into Room DB

                    if (insertedId != -1L) {
                        // Insertion successful, re-fetch the user to get the auto-generated local 'id'.
                        val insertedUser = userRepository.getUserByAuthUid(firebaseUser.uid)
                        if(insertedUser != null) {
                            Log.i("LoginViewModel", "New user created and fetched: ID ${insertedUser.id}")
                            _loginResult.postValue(Event(LoginResult.Success(insertedUser)))
                        } else {
                             // This indicates a potential race condition or DB error.
                             Log.e("LoginViewModel", "Failed to re-fetch user after insert! UID: ${firebaseUser.uid}")
                             _loginResult.postValue(Event(LoginResult.Error("Login failed: Error finalizing account setup.")))
                        }
                    } else {
                         // Insertion failed (e.g., unique constraint violation if email wasn't truly unique).
                         Log.e("LoginViewModel", "Failed to insert new user into local DB! Email: ${newUser.email}")
                         _loginResult.postValue(Event(LoginResult.Error("Login failed: Could not create local account.")))
                    }

                } else {
                    // 4. Local user exists.
                    Log.d("LoginViewModel", "Existing local user found: ID ${localUser.id}, Email: ${localUser.email}, AuthUid: ${localUser.authUid}")
                    // Check if the found local user is missing the Firebase authUid (i.e., was found by email).
                     if(localUser.authUid == null) {
                         // Link the Firebase UID to this existing local account.
                         Log.i("LoginViewModel", "Linking authUid ${firebaseUser.uid} to existing local user ID ${localUser.id}")
                         userRepository.updateUser(localUser.copy(authUid = firebaseUser.uid))
                         // Re-fetch the user to ensure the LiveData observer gets the updated entity.
                         val updatedUser = userRepository.getUserById(localUser.id)
                         _loginResult.postValue(Event(LoginResult.Success(updatedUser ?: localUser))) // Use updated if fetch succeeds
                     } else {
                         // User exists and is already linked correctly, or no update is needed.
                         // Potentially update username/photoUrl here if desired.
                         // Optional: Check if username differs and update
                         // if (localUser.username != firebaseUser.displayName) {
                         //    userRepository.updateUser(localUser.copy(username = firebaseUser.displayName))
                         //    localUser = userRepository.getUserById(localUser.id) ?: localUser // re-fetch
                         // }
                        _loginResult.postValue(Event(LoginResult.Success(localUser)))
                     }
                }
            } catch (e: Exception) {
                // Catch any unexpected exceptions during DB operations.
                Log.e("LoginViewModel", "Database error during handleFirebaseUser", e)
                _loginResult.postValue(Event(LoginResult.Error("Database error during sign-in.")))
            }
        }
    }

    /** Called from the Fragment when the user explicitly initiates sign-out. */
    fun userInitiatedSignOut() {
        Log.d("LoginViewModel", "User initiated sign out.")
        firebaseAuth.signOut() // Sign out from Firebase Authentication.
        // The Fragment is responsible for signing out the GoogleSignInClient and navigating.
    }
}
