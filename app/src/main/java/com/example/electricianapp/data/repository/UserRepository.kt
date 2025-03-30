package com.example.electricianapp.data.repository
import com.example.electricianapp.data.model.UserEntity

/**
 * Interface defining the contract for accessing and manipulating user data.
 * This acts as an abstraction layer between the data sources (local DB, network)
 * and the ViewModels that consume user data.
 */
interface UserRepository {
    /**
     * Inserts a user into the data source.
     * @param user The UserEntity to insert.
     * @return The local database ID of the inserted user, or -1 if insertion failed or was ignored.
     */
    suspend fun insertUser(user: UserEntity): Long

    /**
     * Retrieves a user by their username.
     * @param username The username to search for.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * Retrieves a user by their email address.
     * @param email The email to search for.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Retrieves a user by their Firebase Authentication UID.
     * @param authUid The Firebase UID to search for.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun getUserByAuthUid(authUid: String): UserEntity?

    /**
     * Retrieves a user by their local database ID.
     * @param userId The local database ID.
     * @return The UserEntity if found, null otherwise.
     */
    suspend fun getUserById(userId: Long): UserEntity?

    /**
     * Updates an existing user's information in the data source.
     * @param user The UserEntity with updated data (must contain the correct primary key ID).
     */
    suspend fun updateUser(user: UserEntity)

    // Add other methods as needed, e.g., deleteUser, etc.
}
