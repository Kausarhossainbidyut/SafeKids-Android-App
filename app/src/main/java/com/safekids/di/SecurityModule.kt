package com.safekids.di

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Hilt module for providing security-related dependencies
 * 
 * Note: KeystoreManager and PasswordHasher are auto-provided by Hilt via @Inject constructor
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {
    // All security dependencies are auto-provided via @Inject constructors
}
