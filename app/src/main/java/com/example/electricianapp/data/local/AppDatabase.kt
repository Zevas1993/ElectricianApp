package com.example.electricianapp.data.local // Corrected package

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.electricianapp.data.local.dao.BoxFillDao
import com.example.electricianapp.data.local.dao.ConduitFillDao
import com.example.electricianapp.data.local.dao.DwellingLoadDao
import com.example.electricianapp.data.local.dao.JobDao
import com.example.electricianapp.data.local.dao.MaterialDao
import com.example.electricianapp.data.local.dao.NecCodeDao
import com.example.electricianapp.data.local.dao.PhotoDocDao
import com.example.electricianapp.data.local.dao.VoltageDropDao
// TODO: Add DAOs for Task/User if merging functionality later
// import com.example.electricianapp.data.local.dao.TaskDao
// import com.example.electricianapp.data.local.dao.UserDao
import com.example.electricianapp.data.local.entity.BeforeAfterPairEntity
import com.example.electricianapp.data.local.entity.BoxFillInputEntity
import com.example.electricianapp.data.local.entity.BoxFillResultEntity
import com.example.electricianapp.data.local.entity.CodeViolationCheckEntity
import com.example.electricianapp.data.local.entity.ConduitFillCalculationEntity
import com.example.electricianapp.data.local.entity.DwellingLoadCalculationEntity
import com.example.electricianapp.data.local.entity.JobEntity
import com.example.electricianapp.data.local.entity.MaterialEntity
import com.example.electricianapp.data.local.entity.MaterialInventoryEntity
import com.example.electricianapp.data.local.entity.MaterialListEntity
import com.example.electricianapp.data.local.entity.MaterialListItemEntity
import com.example.electricianapp.data.local.entity.MaterialPackageEntity
import com.example.electricianapp.data.local.entity.MaterialPackageItemEntity
import com.example.electricianapp.data.local.entity.MaterialQuoteEntity
import com.example.electricianapp.data.local.entity.MaterialTransactionEntity
import com.example.electricianapp.data.local.entity.NecArticleEntity
import com.example.electricianapp.data.local.entity.NecBookmarkEntity
import com.example.electricianapp.data.local.entity.NecCodeUpdateEntity
import com.example.electricianapp.data.local.entity.PhotoAnnotationEntity
import com.example.electricianapp.data.local.entity.PhotoDocumentEntity
import com.example.electricianapp.data.local.entity.SupplierEntity
import com.example.electricianapp.data.local.entity.VoltageDropEntity
// TODO: Add Entities for Task/User if merging functionality later
// import com.example.electricianapp.data.model.TaskEntity
// import com.example.electricianapp.data.model.UserEntity
import com.example.electricianapp.data.local.converter.Converters

@Database(
    entities = [
        // Entities for Calculator Features
        BoxFillInputEntity::class,
        BoxFillResultEntity::class,
        ConduitFillCalculationEntity::class,
        DwellingLoadCalculationEntity::class,
        JobEntity::class,
        VoltageDropEntity::class,

        // NEC Code Entities
        NecArticleEntity::class,
        NecCodeUpdateEntity::class,
        NecBookmarkEntity::class,
        CodeViolationCheckEntity::class,

        // Photo Documentation Entities
        PhotoDocumentEntity::class,
        PhotoAnnotationEntity::class,
        BeforeAfterPairEntity::class,

        // Material Management Entities
        MaterialEntity::class,
        MaterialListEntity::class,
        MaterialListItemEntity::class,
        SupplierEntity::class,
        MaterialQuoteEntity::class,
        MaterialInventoryEntity::class,
        MaterialTransactionEntity::class,
        MaterialPackageEntity::class,
        MaterialPackageItemEntity::class,

        // Entities from Job/Task App (Comment out if replacing functionality)
        // com.example.electricianapp.data.model.UserEntity::class,
        // com.example.electricianapp.data.model.JobEntity::class,
        // com.example.electricianapp.data.model.TaskEntity::class
    ],
    version = 2, // Increment version for schema changes
    exportSchema = true // Recommended for version tracking
)
@TypeConverters(Converters::class) // Use the corrected Converters class
abstract class AppDatabase : RoomDatabase() {
    // DAOs for Calculator Features
    abstract fun boxFillDao(): BoxFillDao
    abstract fun conduitFillDao(): ConduitFillDao
    abstract fun dwellingLoadDao(): DwellingLoadDao
    abstract fun jobDao(): JobDao
    abstract fun voltageDropDao(): VoltageDropDao
    abstract fun necCodeDao(): NecCodeDao
    abstract fun photoDocDao(): PhotoDocDao
    abstract fun materialDao(): MaterialDao // Add the abstract function for the material DAO

    // DAOs from Job/Task App (Comment out if replacing functionality)
    // abstract fun userDao(): com.example.electricianapp.data.local.dao.UserDao
    // abstract fun taskDao(): com.example.electricianapp.data.local.dao.TaskDao

    // Note: The Singleton pattern (companion object with getInstance) is often handled
    // by Hilt's @Provides method in the AppModule now, so it's removed from here.
}
