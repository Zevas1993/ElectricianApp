package com.example.electricalcalculator.data.db;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.electricalcalculator.data.db.dao.BoxFillDao;
import com.example.electricalcalculator.data.db.dao.ConduitFillDao;
import com.example.electricalcalculator.data.db.dao.DwellingUnitDao;
import com.example.electricalcalculator.data.db.entity.ApplianceEntity;
import com.example.electricalcalculator.data.db.entity.BoxFillEntity;
import com.example.electricalcalculator.data.db.entity.BoxFillWireCrossRef;
import com.example.electricalcalculator.data.db.entity.ConduitEntity;
import com.example.electricalcalculator.data.db.entity.ConduitFillEntity;
import com.example.electricalcalculator.data.db.entity.ConduitFillWireCrossRef;
import com.example.electricalcalculator.data.db.entity.DwellingUnitEntity;
import com.example.electricalcalculator.data.db.entity.DwellingUnitApplianceCrossRef;
import com.example.electricalcalculator.data.db.entity.WireEntity;

import javax.inject.Inject;
import javax.inject.Singleton;

/**
 * Room database for the Electrician app
 */
@Database(entities = {
        WireEntity.class,
        BoxFillEntity.class,
        BoxFillWireCrossRef.class,
        ConduitEntity.class,
        ConduitFillEntity.class,
        ConduitFillWireCrossRef.class,
        ApplianceEntity.class,
        DwellingUnitEntity.class,
        DwellingUnitApplianceCrossRef.class
}, version = 1)
@Singleton
public abstract class ElectricianDatabase extends RoomDatabase {
    
    private static final String DATABASE_NAME = "electrician_database";
    
    // DAOs
    public abstract BoxFillDao boxFillDao();
    public abstract ConduitFillDao conduitFillDao();
    public abstract DwellingUnitDao dwellingUnitDao();
    
    // Singleton instance
    private static volatile ElectricianDatabase INSTANCE;
    
    /**
     * Get database instance
     * @param context Application context
     * @return Database instance
     */
    public static ElectricianDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (ElectricianDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            ElectricianDatabase.class,
                            DATABASE_NAME)
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}
