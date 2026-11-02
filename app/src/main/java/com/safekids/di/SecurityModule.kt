package com.safekids.di

import android.content.Context
import com.safekids.core.security.KeystoreManager
import com.safekids.core.security.PasswordHasher
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module for providing security-related dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object SecurityModule {

    @Provides
    @Singleton
    fun provideKeystoreManager(
        @ApplicationContext context: Context
    ): KeystoreManager {
        return KeystoreManager(context)
    }

    @Provides
    @Singleton
    fun providePasswordHasher(
        keystoreManager: KeystoreManager
    ): PasswordHasher {
        return PasswordHasher(keystoreManager)
    }
}
