package com.example.electricalcalculator

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Main application class for the Electrician App.
 * This class is used by Hilt for dependency injection.
 */
@HiltAndroidApp
class ElectricianApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize any global components here if needed
    }
}
