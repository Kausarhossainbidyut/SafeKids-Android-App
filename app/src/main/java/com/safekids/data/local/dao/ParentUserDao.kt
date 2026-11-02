package com.safekids.data.local.dao

import androidx.room.*
import com.safekids.data.local.entity.ParentUserEntity
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for Parent Users
 */
@Dao
interface ParentUserDao {
    
    @Query("SELECT * FROM parent_users WHERE phone = :phone LIMIT 1")
    suspend fun findByPhone(phone: String): ParentUserEntity?
    
    @Query("SELECT * FROM parent_users WHERE id = :id LIMIT 1")
    suspend fun findById(id: Long): ParentUserEntity?
    
    @Query("SELECT * FROM parent_users LIMIT 1")
    suspend fun getFirstUser(): ParentUserEntity?
    
    @Query("SELECT COUNT(*) FROM parent_users")
    suspend fun count(): Int
    
    @Query("SELECT EXISTS(SELECT 1 FROM parent_users LIMIT 1)")
    fun hasUsers(): Flow<Boolean>
    
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(user: ParentUserEntity): Long
    
    @Update
    suspend fun update(user: ParentUserEntity)
    
    @Query("UPDATE parent_users SET lastLoginAt = :timestamp WHERE id = :userId")
    suspend fun updateLastLogin(userId: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE parent_users SET passwordSalt = :salt, passwordHash = :hash WHERE id = :userId")
    suspend fun updatePassword(userId: Long, salt: String, hash: String)
    
    @Delete
    suspend fun delete(user: ParentUserEntity)
    
    @Query("DELETE FROM parent_users")
    suspend fun deleteAll()
}
