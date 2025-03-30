package com.example.electricianapp.data.local
import androidx.room.TypeConverter
import com.example.electricianapp.data.model.JobStatus
import java.util.Date

/**
 * Provides type converters for Room database operations.
 * Allows Room to store types it doesn't natively support (like Date and Enums)
 * by converting them to and from supported types (like Long and String).
 * This class needs to be registered with the AppDatabase using `@TypeConverters(Converters::class)`.
 */
class Converters {
    /** Converts a Long timestamp (nullable) stored in the database to a Date object (nullable). */
    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        // If the Long value is not null, create a Date object using it.
        return value?.let { Date(it) }
    }

    /** Converts a Date object (nullable) to its Long timestamp representation (nullable) for database storage. */
    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        // Get the time in milliseconds since the epoch from the Date object.
        return date?.time
    }

    /** Converts a JobStatus enum (nullable) to its String name (nullable) for database storage. */
    @TypeConverter
    fun fromJobStatus(status: JobStatus?): String? {
        // Return the name of the enum constant (e.g., "SCHEDULED").
        return status?.name
    }

    /** Converts a String name (nullable) stored in the database back to a JobStatus enum (nullable). */
    @TypeConverter
    fun toJobStatus(name: String?): JobStatus? {
        // Use enumValueOf safely within a try-catch block.
        // If the stored string doesn't match any enum constant name, return null
        // (or potentially a default value if appropriate).
        return try {
            name?.let { enumValueOf<JobStatus>(it) }
        } catch (e: IllegalArgumentException) {
            // Log the error or handle it if an unknown status string is found in the DB
            null
        }
    }
}
