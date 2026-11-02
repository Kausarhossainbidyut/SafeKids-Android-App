package com.safekids.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.safekids.data.local.dao.ParentUserDao
import com.safekids.data.local.entity.ParentUserEntity
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory

/**
 * Main Room Database for SafeKids App
 * Encrypted using SQLCipher
 */
@Database(
    entities = [
        ParentUserEntity::class
        // More entities will be added in later weeks
    ],
    version = 1,
    exportSchema = true
)
abstract class SafeKidsDatabase : RoomDatabase() {
    
    abstract fun parentUserDao(): ParentUserDao
    
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
