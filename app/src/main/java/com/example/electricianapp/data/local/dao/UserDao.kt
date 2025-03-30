package com.example.electricianapp.data.local.dao
import androidx.room.*
import com.example.electricianapp.data.model.UserEntity

/**
 * Data Access Object (DAO) for User operations.
 * Defines methods for interacting with the 'users' table using Room annotations.
 * These methods will be implemented by Room at compile time.
 * Using 'suspend' for one-shot operations makes them main-safe (Room handles background execution).
 */
@Dao
interface UserDao {
    /**
     * Inserts a user into the database.
     * `OnConflictStrategy.IGNORE`: If a user with the same unique constraints
     * (email or authUid, based on the @Entity definition) already exists,
     * the new insertion attempt is simply ignored.
     *
     * @param user The UserEntity object to insert.
     * @return The SQLite row ID of the newly inserted user, or -1 if the insertion was ignored due to conflict.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(user: UserEntity): Long

    /**
     * Retrieves a user from the database based on their username.
     * Limits the result to 1, though username might not be unique by schema.
     *
     * @param username The username to search for.
     * @return The matching UserEntity object, or null if no user is found with that username.
     */
    @Query("SELECT * FROM users WHERE username = :username LIMIT 1")
    suspend fun getUserByUsername(username: String): UserEntity?

    /**
     * Retrieves a user from the database based on their email address.
     * Email is indexed and unique.
     *
     * @param email The email address to search for.
     * @return The matching UserEntity object, or null if no user is found with that email.
     */
    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    suspend fun getUserByEmail(email: String): UserEntity?

    /**
     * Retrieves a user from the database based on their Firebase Authentication User ID (UID).
     * AuthUid is indexed and unique.
     *
     * @param authUid The Firebase UID to search for.
     * @return The matching UserEntity object, or null if no user is found with that UID.
     */
    @Query("SELECT * FROM users WHERE authUid = :authUid LIMIT 1")
    suspend fun getUserByAuthUid(authUid: String): UserEntity?

    /**
     * Retrieves a user from the database based on their local database primary key ID.
     *
     * @param userId The local ID of the user.
     * @return The matching UserEntity object, or null if no user is found with that ID.
     */
    @Query("SELECT * FROM users WHERE id = :userId LIMIT 1")
    suspend fun getUserById(userId: Long): UserEntity?

    /**
     * Updates an existing user in the database.
     * Room identifies the user to update based on the primary key (`id`) of the passed UserEntity object.
     *
     * @param user The UserEntity object with updated information.
     */
    @Update
    suspend fun update(user: UserEntity)

    // Optional: Delete function if user deletion is needed within the app.
    // @Delete
    // suspend fun delete(user: UserEntity)
}
