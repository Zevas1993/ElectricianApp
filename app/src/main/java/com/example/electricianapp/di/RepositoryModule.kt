package com.example.electricianapp.di

// Remove wildcard import
import com.example.electricianapp.data.repository.TaskRepositoryImpl // Explicit import
// import com.example.electricianapp.data.repository.UserRepositoryImpl // Commented out as file is missing
import com.example.electricianapp.data.repository.boxfill.BoxFillRepositoryImpl // Specific import
import com.example.electricianapp.data.repository.conduitfill.ConduitFillRepositoryImpl // Specific import
import com.example.electricianapp.data.repository.dwellingload.DwellingLoadRepositoryImpl // Specific import
import com.example.electricianapp.data.repository.jobs.JobRepositoryImpl // Explicit import for new Job Repo Impl
import com.example.electricianapp.data.repository.MaterialRepositoryImpl // Import for Material repo impl
import com.example.electricianapp.data.repository.neccodes.NecCodeRepositoryImpl // Import for NEC code repo
import com.example.electricianapp.data.repository.photodoc.PhotoDocRepositoryImpl // Import for photo documentation repo
import com.example.electricianapp.data.repository.voltagedrop.VoltageDropRepositoryImpl // Import for voltage drop repo
// import com.example.electricianapp.domain.repository.TaskRepository // Commented out as file is missing or incorrect
// import com.example.electricianapp.domain.repository.UserRepository // Commented out as file is missing
import com.example.electricianapp.domain.repository.boxfill.BoxFillRepository // Specific import
import com.example.electricianapp.domain.repository.conduitfill.ConduitFillRepository // Specific import
import com.example.electricianapp.domain.repository.dwellingload.DwellingLoadRepository // Specific import
import com.example.electricianapp.domain.repository.jobs.JobRepository // Explicit import for new Job Repo Interface
import com.example.electricianapp.domain.repository.materials.MaterialRepository // Import for Material repo interface
import com.example.electricianapp.domain.repository.neccodes.NecCodeRepository // Import for NEC code repo interface
import com.example.electricianapp.domain.repository.photodoc.PhotoDocRepository // Import for photo documentation repo interface
import com.example.electricianapp.domain.repository.voltagedrop.VoltageDropRepository // Import for voltage drop repo interface
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

    /* TODO: Uncomment when UserRepository files exist
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
    */

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

    /* TODO: Uncomment when TaskRepository files exist/are correct
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
    */

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

    /**
     * Binds the BoxFillRepository interface to its concrete implementation BoxFillRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindBoxFillRepository(
        boxFillRepositoryImpl: BoxFillRepositoryImpl
    ): BoxFillRepository

    /**
     * Binds the VoltageDropRepository interface to its concrete implementation VoltageDropRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindVoltageDropRepository(
        voltageDropRepositoryImpl: VoltageDropRepositoryImpl
    ): VoltageDropRepository

    /**
     * Binds the NecCodeRepository interface to its concrete implementation NecCodeRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindNecCodeRepository(
        necCodeRepositoryImpl: NecCodeRepositoryImpl
    ): NecCodeRepository

    /**
     * Binds the PhotoDocRepository interface to its concrete implementation PhotoDocRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindPhotoDocRepository(
        photoDocRepositoryImpl: PhotoDocRepositoryImpl
    ): PhotoDocRepository

    /**
     * Binds the MaterialRepository interface to its concrete implementation MaterialRepositoryImpl.
     */
    @Binds
    @Singleton
    abstract fun bindMaterialRepository(
        materialRepositoryImpl: MaterialRepositoryImpl
    ): MaterialRepository
}
