package com.safekids

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Application class for SafeKids
 * 
 * Annotated with @HiltAndroidApp to trigger Hilt's code generation
 * and serves as the application-level dependency container.
 */
@HiltAndroidApp
class SafeKidsApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        
        // Initialize application-wide components here
        // Security components are initialized lazily via Hilt
    }
}
