package com.safekids.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.safekids.data.local.dao.*
import com.safekids.data.local.entity.*
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Main Room Database for SafeKids App
 * Encrypted using SQLCipher
 */
@Database(
    entities = [
        ParentUserEntity::class,
        AppUsageEntity::class,
        InstalledAppEntity::class,
        TimeLimitEntity::class,
        BlockLogEntity::class
    ],
    version = 2, // Incremented from 1 to 2
    exportSchema = true
)
abstract class SafeKidsDatabase : RoomDatabase() {
    
    abstract fun parentUserDao(): ParentUserDao
    abstract fun appUsageDao(): AppUsageDao
    abstract fun installedAppDao(): InstalledAppDao
    abstract fun timeLimitDao(): TimeLimitDao
    abstract fun blockLogDao(): BlockLogDao
    
    companion object {
        private const val DATABASE_NAME = "safekids.db"
        
        @Volatile
        private var INSTANCE: SafeKidsDatabase? = null
        
        fun getInstance(context: Context, passphrase: ByteArray): SafeKidsDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = buildDatabase(context, passphrase)
                INSTANCE = instance
                instance
            }
        }
        
        private fun buildDatabase(context: Context, passphrase: ByteArray): SafeKidsDatabase {
            val factory = SupportFactory(passphrase)
            
            return Room.databaseBuilder(
                context.applicationContext,
                SafeKidsDatabase::class.java,
                DATABASE_NAME
            )
                .openHelperFactory(factory)
                .fallbackToDestructiveMigration() // TODO: Proper migrations in production
                .build()
        }
        
        fun closeDatabase() {
            INSTANCE?.close()
            INSTANCE = null
        }
    }
}
