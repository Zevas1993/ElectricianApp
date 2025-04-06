package com.example.electricianapp.di
import com.example.electricianapp.data.repository.* // Import interfaces and implementations
import com.example.electricianapp.data.repository.conduitfill.ConduitFillRepositoryImpl // Specific import
import com.example.electricianapp.data.repository.dwellingload.DwellingLoadRepositoryImpl // Specific import
import com.example.electricianapp.domain.repository.conduitfill.ConduitFillRepository // Specific import
import com.example.electricianapp.domain.repository.dwellingload.DwellingLoadRepository // Specific import
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module responsible for providing Repository dependencies.
 * Uses @Binds for telling Hilt which concrete implementation to use when an interface is requested.
 * This promotes coding against interfaces.
 *
 * @Module Marks this as a Hilt module.
 * @InstallIn(SingletonComponent::class) Specifies application-wide singleton scope.
 * `abstract class` is typically used with @Binds methods.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    /**
     * Binds the UserRepository interface to its concrete implementation UserRepositoryImpl.
     * When a component requests `UserRepository`, Hilt will provide an instance of `UserRepositoryImpl`.
     *
     * @param userRepositoryImpl An instance of the implementation class (Hilt knows how to create this because it's annotated with @Inject constructor).
     * @return The bound UserRepository interface.
     */
    @Binds
    @Singleton // Ensures a single instance of the repository throughout the app lifecycle.
    abstract fun bindUserRepository(
        userRepositoryImpl: UserRepositoryImpl
    ): UserRepository

    /**
     * Binds the JobRepository interface to its concrete implementation JobRepositoryImpl.
     *
     * @param jobRepositoryImpl An instance of the implementation class.
     * @return The bound JobRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindJobRepository(
        jobRepositoryImpl: JobRepositoryImpl
    ): JobRepository

    /**
     * Binds the TaskRepository interface to its concrete implementation TaskRepositoryImpl.
     *
     * @param taskRepositoryImpl An instance of the implementation class.
     * @return The bound TaskRepository interface.
     */
    @Binds
    @Singleton
    abstract fun bindTaskRepository(
        taskRepositoryImpl: TaskRepositoryImpl
    ): TaskRepository

    /**
     * Binds the ConduitFillRepository interface to its concrete implementation ConduitFillRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindConduitFillRepository(
        conduitFillRepositoryImpl: ConduitFillRepositoryImpl
    ): ConduitFillRepository

    /**
     * Binds the DwellingLoadRepository interface to its concrete implementation DwellingLoadRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindDwellingLoadRepository(
        dwellingLoadRepositoryImpl: DwellingLoadRepositoryImpl
    ): DwellingLoadRepository
}
