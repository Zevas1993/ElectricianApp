package com.example.electricalcalculator

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

/**
 * Custom Application class for the ElectricianApp to initialize
 * dependencies and setup components like Koin.
 */
class ElectricianApplication : Application() {
    
    override fun onCreate() {
        super.onCreate()
        
        // Initialize Koin Dependency Injection
        startKoin {
            // Use Android logger for debug builds
            androidLogger(Level.ERROR) // Set to ERROR to avoid Kotlin reflection issues
            
            // Provide Android context
            androidContext(this@ElectricianApplication)
            
            // Load Koin modules
            // modules(appModule, domainModule, dataModule, featureModules)
            // Note: Actual modules will be defined and loaded here
        }
        
        // Initialize other app-wide components if needed
    }
}
