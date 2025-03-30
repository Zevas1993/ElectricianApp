package com.example.electricianapp.data.repository
import android.util.Log
import com.example.electricianapp.data.local.dao.UserDao
import com.example.electricianapp.data.model.UserEntity
// import com.google.firebase.firestore.FirebaseFirestore // Uncomment if syncing
// import kotlinx.coroutines.tasks.await // For await() on Firestore tasks if syncing
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Concrete implementation of the UserRepository interface.
 * Currently, it only interacts with the local Room database via UserDao.
 * Marked as @Singleton to ensure only one instance exists, managed by Hilt.
 *
 * @Inject constructor Tells Hilt how to create an instance of this class.
 *   Hilt will provide the required UserDao dependency (defined in DatabaseModule).
 */
@Singleton
class UserRepositoryImpl @Inject constructor(
    private val userDao: UserDao
    // private val firestore: FirebaseFirestore // Uncomment and inject if syncing user data
) : UserRepository {

    private val TAG = "UserRepositoryImpl" // For logging

    override suspend fun insertUser(user: UserEntity): Long {
        Log.d(TAG, "Attempting to insert user: ${user.email}")
        val localId = userDao.insert(user)
        if (localId == -1L) {
            Log.w(TAG, "User insertion ignored (likely conflict): ${user.email}")
        } else {
            Log.i(TAG, "User inserted locally with ID: $localId, Email: ${user.email}")
            // ** Firestore Sync (Optional Example) **
            /*
            if (user.authUid != null) {
                try {
                    firestore.collection("users").document(user.authUid).set(user.copy(id=0)).await() // Store copy without local ID
                    Log.i(TAG, "User data synced to Firestore for UID: ${user.authUid}")
                } catch (e: Exception) {
                    Log.e(TAG, "Error syncing new user to Firestore for UID: ${user.authUid}", e)
                    // Handle sync error (e.g., queue for later, notify user?)
                }
            } else {
                 Log.w(TAG, "Cannot sync user to Firestore: authUid is null for ${user.email}")
            }
            */
        }
        return localId
    }

    override suspend fun getUserByUsername(username: String): UserEntity? {
        // In a sync scenario, you might check local DB first, then Firestore if needed.
        return userDao.getUserByUsername(username)
    }

    override suspend fun getUserByEmail(email: String): UserEntity? {
        return userDao.getUserByEmail(email)
    }

    override suspend fun getUserByAuthUid(authUid: String): UserEntity? {
        return userDao.getUserByAuthUid(authUid)
    }

    override suspend fun getUserById(userId: Long): UserEntity? {
        return userDao.getUserById(userId)
    }

    override suspend fun updateUser(user: UserEntity) {
        Log.d(TAG, "Attempting to update user ID: ${user.id}, Email: ${user.email}")
        userDao.update(user)
        Log.i(TAG, "User updated locally: ID ${user.id}")
        // ** Firestore Sync (Optional Example) **
        /*
        if (user.authUid != null) {
             try {
                firestore.collection("users").document(user.authUid).set(user.copy(id=0), SetOptions.merge()).await() // Merge updates
                Log.i(TAG, "User data update synced to Firestore for UID: ${user.authUid}")
             } catch (e: Exception) {
                 Log.e(TAG, "Error syncing user update to Firestore for UID: ${user.authUid}", e)
                 // Handle sync error
             }
        }
        */
    }
}
