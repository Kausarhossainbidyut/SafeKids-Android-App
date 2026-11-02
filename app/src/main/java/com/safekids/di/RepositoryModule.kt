package com.safekids.di

import com.safekids.core.security.PasswordHasher
import com.safekids.data.local.dao.*
import com.safekids.data.repository.*
import com.safekids.domain.repository.*
import com.safekids.util.InstalledAppsManager
import com.safekids.util.UsageStatsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        parentUserDao: ParentUserDao,
        passwordHasher: PasswordHasher
    ): AuthRepository {
        return AuthRepositoryImpl(parentUserDao, passwordHasher)
    }
    
    @Provides
    @Singleton
    fun provideUsageStatsRepository(
        appUsageDao: AppUsageDao,
        usageStatsManager: UsageStatsManager
    ): UsageStatsRepository {
        return UsageStatsRepositoryImpl(appUsageDao, usageStatsManager)
    }
    
    @Provides
    @Singleton
    fun provideTimeLimitRepository(
        timeLimitDao: TimeLimitDao,
        blockLogDao: BlockLogDao
    ): TimeLimitRepository {
        return TimeLimitRepositoryImpl(timeLimitDao, blockLogDao)
    }
    
    @Provides
    @Singleton
    fun provideAppManagementRepository(
        installedAppDao: InstalledAppDao,
        installedAppsManager: InstalledAppsManager
    ): AppManagementRepository {
        return AppManagementRepositoryImpl(installedAppDao, installedAppsManager)
    }
}
