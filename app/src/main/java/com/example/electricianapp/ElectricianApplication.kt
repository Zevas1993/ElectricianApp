package com.example.electricianapp // Corrected package

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Custom Application class required for Hilt setup.
 * The @HiltAndroidApp annotation triggers Hilt's code generation,
 * including a base class for your application that serves as the
 * application-level dependency container.
 */
@HiltAndroidApp
class ElectricianApplication : Application() {
    // No need to manually initialize Hilt here; the annotation handles it.
    // You can add other application-wide initialization logic in onCreate if needed.
    override fun onCreate() {
        super.onCreate()
        // Example: Initialize Timber logging, analytics, etc.
    }
}
