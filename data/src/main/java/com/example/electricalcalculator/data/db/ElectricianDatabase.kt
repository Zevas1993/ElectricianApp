package com.example.electricalcalculator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
// import androidx.room.TypeConverters // Removed - Converters class missing
// import com.example.electricalcalculator.data.db.converter.Converters // Removed - Converters class missing
// import com.example.electricalcalculator.data.db.dao.* // Removed - DAOs missing
import com.example.electricalcalculator.data.db.entity.BoxFillEntity
import com.example.electricalcalculator.data.db.entity.BoxFillWireCrossRef
import com.example.electricalcalculator.data.db.entity.WireEntity

/**
 * Main database class for the Electrician App.
 * Contains all DAOs and entities.
 */
@Database(
    entities = [
        WireEntity::class,
        BoxFillEntity::class, 
        BoxFillWireCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
abstract class ElectricianDatabase : RoomDatabase() {
    
    // DAOs removed - Interfaces missing
    // abstract fun wireDao(): WireDao
    // abstract fun boxFillDao(): BoxFillDao
    
    companion object {
        @Volatile
        private var INSTANCE: ElectricianDatabase? = null
        
        fun getDatabase(context: Context): ElectricianDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    ElectricianDatabase::class.java,
                    "electrician_database"
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
