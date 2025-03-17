package com.example.electricalcalculator.data.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.electricalcalculator.data.db.converter.Converters
import com.example.electricalcalculator.data.db.dao.*
import com.example.electricalcalculator.data.db.entity.*

/**
 * Main database class for the Electrician App.
 * Contains all DAOs and entities.
 */
@Database(
    entities = [
        WireEntity::class,
        BoxFillEntity::class, 
        BoxFillWireCrossRef::class,
        ConduitEntity::class,
        ConduitFillEntity::class,
        ConduitFillWireCrossRef::class,
        ApplianceEntity::class,
        DwellingUnitEntity::class,
        DwellingUnitApplianceCrossRef::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ElectricianDatabase : RoomDatabase() {
    
    // DAOs
    abstract fun wireDao(): WireDao
    abstract fun boxFillDao(): BoxFillDao
    abstract fun conduitDao(): ConduitDao
    abstract fun conduitFillDao(): ConduitFillDao
    abstract fun applianceDao(): ApplianceDao
    abstract fun dwellingUnitDao(): DwellingUnitDao
    
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
