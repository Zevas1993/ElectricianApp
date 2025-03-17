package com.example.electricalcalculator.di

import android.content.Context
import com.example.electricalcalculator.data.db.ElectricianDatabase
import com.example.electricalcalculator.data.db.dao.*
import com.example.electricalcalculator.data.repository.*
import com.example.electricalcalculator.domain.repository.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing database related dependencies.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    
    /**
     * Provides the Room database instance.
     */
    @Singleton
    @Provides
    fun provideDatabase(@ApplicationContext appContext: Context): ElectricianDatabase {
        return ElectricianDatabase.getDatabase(appContext)
    }
    
    /**
     * Provides the WireDao instance.
     */
    @Provides
    fun provideWireDao(database: ElectricianDatabase): WireDao {
        return database.wireDao()
    }
    
    /**
     * Provides the BoxFillDao instance.
     */
    @Provides
    fun provideBoxFillDao(database: ElectricianDatabase): BoxFillDao {
        return database.boxFillDao()
    }
    
    /**
     * Provides the ConduitDao instance.
     */
    @Provides
    fun provideConduitDao(database: ElectricianDatabase): ConduitDao {
        return database.conduitDao()
    }
    
    /**
     * Provides the ConduitFillDao instance.
     */
    @Provides
    fun provideConduitFillDao(database: ElectricianDatabase): ConduitFillDao {
        return database.conduitFillDao()
    }
    
    /**
     * Provides the ApplianceDao instance.
     */
    @Provides
    fun provideApplianceDao(database: ElectricianDatabase): ApplianceDao {
        return database.applianceDao()
    }
    
    /**
     * Provides the DwellingUnitDao instance.
     */
    @Provides
    fun provideDwellingUnitDao(database: ElectricianDatabase): DwellingUnitDao {
        return database.dwellingUnitDao()
    }
    
    /**
     * Provides the WireRepository implementation.
     */
    @Singleton
    @Provides
    fun provideWireRepository(wireDao: WireDao): WireRepository {
        return WireRepositoryImpl(wireDao)
    }
    
    /**
     * Provides the BoxFillRepository implementation.
     */
    @Singleton
    @Provides
    fun provideBoxFillRepository(boxFillDao: BoxFillDao): BoxFillRepository {
        return BoxFillRepositoryImpl(boxFillDao)
    }
    
    /**
     * Provides the ConduitFillRepository implementation.
     */
    @Singleton
    @Provides
    fun provideConduitFillRepository(
        conduitFillDao: ConduitFillDao,
        conduitDao: ConduitDao
    ): ConduitFillRepository {
        return ConduitFillRepositoryImpl(conduitFillDao, conduitDao)
    }
    
    /**
     * Provides the DwellingLoadRepository implementation.
     */
    @Singleton
    @Provides
    fun provideDwellingLoadRepository(
        dwellingUnitDao: DwellingUnitDao,
        applianceDao: ApplianceDao
    ): DwellingLoadRepository {
        return DwellingLoadRepositoryImpl(dwellingUnitDao, applianceDao)
    }
}
