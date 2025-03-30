package com.example.electricianapp // Adjust package name if needed
import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Base Application class for the ElectricianApp.
 * Required by Hilt for dependency injection setup.
 * Annotated with @HiltAndroidApp to enable Hilt's code generation.
 * This class is specified in the AndroidManifest.xml <application> tag's android:name attribute.
 * You can also place application-wide initialization code here (like logging setup) if needed.
 */
@HiltAndroidApp
class ElectricianApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialization code here, e.g.:
        // if (BuildConfig.DEBUG) {
        //     Timber.plant(Timber.DebugTree())
        // }
        // Analytics.init(this)
        // etc.
    }
}
