package com.safekids.di

import com.safekids.core.security.PasswordHasher
import com.safekids.data.local.dao.ParentUserDao
import com.safekids.data.repository.AuthRepositoryImpl
import com.safekids.domain.repository.AuthRepository
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
}
