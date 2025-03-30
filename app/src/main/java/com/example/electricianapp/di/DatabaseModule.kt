package com.example.electricianapp.di
import android.content.Context
import com.example.electricianapp.data.local.AppDatabase
import com.example.electricianapp.data.local.dao.JobDao
import com.example.electricianapp.data.local.dao.TaskDao
import com.example.electricianapp.data.local.dao.UserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module responsible for providing database-related dependencies.
 *
 * @Module Marks this class as a Hilt module.
 * @InstallIn(SingletonComponent::class) Specifies that the bindings defined in this module
 *   will be available application-wide and have a singleton scope (only one instance created).
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule { // Use 'object' for modules providing dependencies via @Provides

    /**
     * Provides the singleton instance of the AppDatabase.
     * Hilt automatically provides the ApplicationContext needed here.
     *
     * @param appContext The application context provided by Hilt.
     * @return A singleton instance of AppDatabase.
     */
    @Provides
    @Singleton // Ensures only one instance of the database is created
    fun provideAppDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.getDatabase(appContext)
    }

    /**
     * Provides the UserDao instance.
     * Depends on the AppDatabase instance provided by `provideAppDatabase`.
     * Hilt figures out the dependency graph.
     *
     * @param db The AppDatabase instance provided by Hilt.
     * @return An instance of UserDao.
     */
    @Provides
    // No @Singleton needed here if the DAO itself doesn't maintain state and AppDatabase is singleton.
    // Room DAOs are typically lightweight.
    fun provideUserDao(db: AppDatabase): UserDao {
        return db.userDao()
    }

    /**
     * Provides the JobDao instance.
     * Depends on the AppDatabase instance.
     *
     * @param db The AppDatabase instance provided by Hilt.
     * @return An instance of JobDao.
     */
    @Provides
    fun provideJobDao(db: AppDatabase): JobDao {
        return db.jobDao()
    }

    /**
     * Provides the TaskDao instance.
     * Depends on the AppDatabase instance.
     *
     * @param db The AppDatabase instance provided by Hilt.
     * @return An instance of TaskDao.
     */
    @Provides
    fun provideTaskDao(db: AppDatabase): TaskDao {
        return db.taskDao()
    }
}
