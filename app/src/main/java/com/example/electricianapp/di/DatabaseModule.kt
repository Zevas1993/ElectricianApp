package com.example.electricianapp.di // Corrected package

import android.content.Context
import androidx.room.Room
import com.example.electricianapp.data.local.AppDatabase // Corrected import
import com.example.electricianapp.data.local.dao.JobDao // Corrected import
import com.example.electricianapp.data.local.dao.TaskDao // Corrected import
import com.example.electricianapp.data.local.dao.UserDao // Corrected import
// TODO: Import calculator DAOs when entities and AppDatabase are updated
import com.example.electricianapp.data.local.dao.BoxFillDao // Uncomment import
import com.example.electricianapp.data.local.dao.ConduitFillDao // Uncomment import
import com.example.electricianapp.data.local.dao.DwellingLoadDao // Uncomment import
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
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        // Use the AppDatabase class we defined earlier
        return Room.databaseBuilder(
            appContext,
            AppDatabase::class.java,
            "electrician_app_database" // Consistent DB name
        )
        .fallbackToDestructiveMigration() // Suitable for development
        .build()
        // Note: Removed the manual singleton implementation as Hilt handles it with @Singleton
    }

    // --- DAOs for Job/Task App ---
    /* TODO: Uncomment when User/Task functionality is added
    @Provides
    fun provideUserDao(database: AppDatabase): UserDao {
        return database.userDao()
    }
    */

    @Provides // Uncomment JobDao provider
    fun provideJobDao(database: AppDatabase): JobDao {
        return database.jobDao()
    }

    /* TODO: Uncomment when Task functionality is added
    @Provides
    fun provideTaskDao(database: AppDatabase): TaskDao {
        return database.taskDao()
    }
    */

    // --- DAOs for Calculator Features (Commented out until needed) ---
    // Keep BoxFillDao commented // Remove this comment line
    @Provides // Uncomment this provider
    fun provideBoxFillDao(database: AppDatabase): BoxFillDao {
        return database.boxFillDao()
    }

    // Keep ConduitFillDao commented // Remove this comment line
    @Provides // Uncomment this provider
    fun provideConduitFillDao(database: AppDatabase): ConduitFillDao {
        return database.conduitFillDao()
    }


    @Provides // Uncomment this provider
    fun provideDwellingLoadDao(database: AppDatabase): DwellingLoadDao {
        return database.dwellingLoadDao()
    }


    // --- Repositories (Moved to RepositoryModule) ---
    // Providing repositories here is possible, but binding interfaces to implementations
    // in a separate RepositoryModule (like we did) is often preferred.
    // These @Provides methods are commented out assuming RepositoryModule handles bindings.

    /*
    @Singleton
    @Provides
    fun provideWireRepository(wireDao: WireDao): WireRepository {
        // Assuming WireRepositoryImpl exists and takes WireDao
        return WireRepositoryImpl(wireDao)
    }

    @Singleton
    @Provides
    fun provideBoxFillRepository(boxFillDao: BoxFillDao): BoxFillRepository {
         // Assuming BoxFillRepositoryImpl exists and takes BoxFillDao
        return BoxFillRepositoryImpl(boxFillDao)
    }

    @Singleton
    @Provides
    fun provideConduitFillRepository(conduitFillDao: ConduitFillDao): ConduitFillRepository {
         // Assuming ConduitFillRepositoryImpl exists and takes ConduitFillDao
        return ConduitFillRepositoryImpl(conduitFillDao) // Might need other DAOs too
    }

     @Singleton
     @Provides
     fun provideDwellingLoadRepository(dwellingLoadDao: DwellingLoadDao): DwellingLoadRepository {
          // Assuming DwellingLoadRepositoryImpl exists and takes DwellingLoadDao
         return DwellingLoadRepositoryImpl(dwellingLoadDao) // Might need other DAOs too
     }
     */
}
