package com.example.electricianapp.data.local // Corrected package

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.electricianapp.data.local.dao.BoxFillDao // Corrected import path
import com.example.electricianapp.data.local.dao.ConduitFillDao // Corrected import path
import com.example.electricianapp.data.local.dao.DwellingLoadDao // Corrected import path
import com.example.electricianapp.data.local.dao.JobDao // Import the new DAO
// TODO: Add DAOs for Task/User if merging functionality later
// import com.example.electricianapp.data.local.dao.TaskDao
// import com.example.electricianapp.data.local.dao.UserDao
import com.example.electricianapp.data.local.entity.BoxFillInputEntity // Corrected import path
import com.example.electricianapp.data.local.entity.BoxFillResultEntity // Corrected import path
import com.example.electricianapp.data.local.entity.ConduitFillCalculationEntity // Corrected import path for the new entity
import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity // Import the new entity
import com.example.electricianapp.data.local.entity.JobEntity // Import the new entity
// TODO: Add Entities for Task/User if merging functionality later
// import com.example.electricianapp.data.model.TaskEntity
// import com.example.electricianapp.data.model.UserEntity
import com.example.electricianapp.data.local.converter.Converters // Corrected import path

@Database(
    entities = [
        // Entities for Calculator Features
        BoxFillInputEntity::class, // Assuming these exist or will be created
        BoxFillResultEntity::class, // Assuming these exist or will be created
        ConduitFillCalculationEntity::class, // Use the correct entity class
        DwellingLoadCalculationEntity::class, // Keep this one
        JobEntity::class, // Add the new Job entity

        // Entities from Job/Task App (Comment out if replacing functionality)
        // com.example.electricianapp.data.model.UserEntity::class,
        // com.example.electricianapp.data.model.JobEntity::class,
        // com.example.electricianapp.data.model.TaskEntity::class
    ],
    version = 1, // Start with version 1. Increment on schema changes.
    exportSchema = true // Recommended for version tracking
)
@TypeConverters(Converters::class) // Use the corrected Converters class
abstract class AppDatabase : RoomDatabase() {
    // DAOs for Calculator Features
    abstract fun boxFillDao(): BoxFillDao // Assuming this exists or will be created
    abstract fun conduitFillDao(): ConduitFillDao // Assuming this exists or will be created
    abstract fun dwellingLoadDao(): DwellingLoadDao // Assuming this exists or will be created
    abstract fun jobDao(): JobDao // Add the abstract function for the new DAO

    // DAOs from Job/Task App (Comment out if replacing functionality)
    // abstract fun userDao(): com.example.electricianapp.data.local.dao.UserDao
    // abstract fun taskDao(): com.example.electricianapp.data.local.dao.TaskDao

    // Note: The Singleton pattern (companion object with getInstance) is often handled
    // by Hilt's @Provides method in the AppModule now, so it's removed from here.
}
