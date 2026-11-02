package com.safekids.di

import android.content.Context
import androidx.room.Room
import com.safekids.data.local.SafeKidsDatabase
import com.safekids.data.local.dao.ParentUserDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

/**
 * Hilt module for providing database dependencies
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideSafeKidsDatabase(
        @ApplicationContext context: Context
    ): SafeKidsDatabase {
        // Generate encryption key from device-specific data
        val passphrase = SQLiteDatabase.getBytes("safekids_db_key_2024".toCharArray())
        val factory = SupportFactory(passphrase)

        return Room.databaseBuilder(
            context,
            SafeKidsDatabase::class.java,
            "safekids_database"
        )
            .openHelperFactory(factory)
            .fallbackToDestructiveMigration() // For development only
            .build()
    }

    @Provides
    @Singleton
    fun provideParentUserDao(database: SafeKidsDatabase): ParentUserDao {
        return database.parentUserDao()
    }
}
