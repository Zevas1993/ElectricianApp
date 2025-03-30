package com.example.electricianapp.data.local
import android.content.Context
import androidx.room.*
import com.example.electricianapp.data.local.dao.* // Import all DAOs
import com.example.electricianapp.data.model.* // Import all Entities

/**
 * The main Room database class for the application.
 *
 * Annotated with @Database to define the entities included and the database version.
 * Annotated with @TypeConverters to register custom converters (e.g., for Date, Enums).
 * Provides abstract methods to access the Data Access Objects (DAOs).
 * Implements a Singleton pattern using a companion object to ensure only one
 * instance of the database exists throughout the app's lifecycle.
 */
@Database(
    entities = [
        UserEntity::class,  // List all entities included in this database
        JobEntity::class,
        TaskEntity::class
    ],
    version = 1, // Start with version 1. **Increment this number whenever you change the schema** (add/remove/modify tables or columns).
    exportSchema = true // Recommended: Exports the schema to a JSON file in your project (usually app/schemas/). Helps with version tracking and testing migrations.
)
@TypeConverters(Converters::class) // Register the Converters class to handle Date <-> Long and JobStatus <-> String conversions.
abstract class AppDatabase : RoomDatabase() {

    // Abstract methods that Room will implement to provide access to each DAO.
    abstract fun userDao(): UserDao
    abstract fun jobDao(): JobDao
    abstract fun taskDao(): TaskDao

    /** Companion object for static access, primarily for the Singleton database instance. */
    companion object {
        // @Volatile: Ensures that the value of INSTANCE is always up-to-date and the same to all execution threads.
        // It means that writes to this field are immediately made visible to other threads.
        @Volatile private var INSTANCE: AppDatabase? = null

        /**
         * Gets the singleton database instance. If it doesn't exist, creates it.
         * Uses synchronized block to ensure thread safety during instance creation.
         *
         * @param context The application context.
         * @return The singleton AppDatabase instance.
         */
        fun getDatabase(context: Context): AppDatabase {
            // Return the existing instance if it's already created.
            return INSTANCE ?: synchronized(this) {
                // If instance is still null inside the synchronized block, create it.
                val instance = Room.databaseBuilder(
                    context.applicationContext, // Use application context to avoid leaks
                    AppDatabase::class.java,
                    "electrician_app_database" // Define the name for the database file
                )
                // ** Migration Strategy **
                // `fallbackToDestructiveMigration()`: If Room needs to migrate to a new version
                // and no specific `Migration` path is provided, it will simply delete all
                // existing tables and data, then recreate the schema.
                // **WARNING:** Suitable for development ONLY. Data will be lost on schema change.
                // Replace with `.addMigrations(MIGRATION_X_Y, ...)` for production releases.
                .fallbackToDestructiveMigration()
                // Example of adding a proper migration (requires defining MIGRATION_1_2 etc.)
                // .addMigrations(MIGRATION_1_2)
                .build() // Builds the database instance.

                INSTANCE = instance // Assign the newly created instance to the static field.
                instance // Return the instance.
            }
        }

        // Example of defining a Migration (needed for production instead of fallback)
        /*
        val MIGRATION_1_2: Migration = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                // Example: Add a 'notes' column to the 'jobs' table in version 2
                db.execSQL("ALTER TABLE jobs ADD COLUMN notes TEXT")
            }
        }
        */

    }
}
