package com.safekids.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Parent User
 * Stores parent account information in encrypted local database
 */
@Entity(
    tableName = "parent_users",
    indices = [Index(value = ["phone"], unique = true)]
)
data class ParentUserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val name: String,
    
    val phone: String, // Used as unique identifier for login
    
    val passwordSalt: String, // Base64 encoded salt
    
    val passwordHash: String, // Base64 encoded hash (PBKDF2)
    
    val recoveryCodeHash: String? = null, // Base64 encoded recovery code hash
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val lastLoginAt: Long? = null
)
