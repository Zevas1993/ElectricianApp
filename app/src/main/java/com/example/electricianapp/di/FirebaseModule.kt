package com.example.electricianapp.di
import com.google.firebase.auth.FirebaseAuth
// import com.google.firebase.firestore.FirebaseFirestore // Uncomment if using Firestore sync later
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt Module for providing Firebase related dependencies.
 * Kept separate from DatabaseModule for better organization.
 *
 * @Module Marks this as a Hilt module.
 * @InstallIn(SingletonComponent::class) Dependencies provided here are application-scoped singletons.
 */
@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    /**
     * Provides a singleton instance of FirebaseAuth.
     * `FirebaseAuth.getInstance()` typically returns a singleton itself.
     *
     * @return The singleton FirebaseAuth instance.
     */
    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    /*
    // ** Uncomment this block if you add Firestore synchronization later **
    // Provides a singleton instance of FirebaseFirestore.
    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        // Basic instance. You could add settings here if needed.
        // val settings = FirebaseFirestoreSettings.Builder()
        //    .setPersistenceEnabled(true) // Enable offline caching (recommended)
        //    .build()
        // val firestore = FirebaseFirestore.getInstance()
        // firestore.firestoreSettings = settings
        // return firestore
        return FirebaseFirestore.getInstance()
    }
    */
}
