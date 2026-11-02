package com.safekids.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Installed Apps
 * Stores information about apps installed on device
 */
@Entity(
    tableName = "installed_apps",
    indices = [
        Index(value = ["packageName"], unique = true)
    ]
)
data class InstalledAppEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val packageName: String,
    
    val appName: String,
    
    val category: String = "OTHER", // Categories: GAME, SOCIAL, EDUCATION, PRODUCTIVITY, OTHER
    
    val isSystemApp: Boolean = false,
    
    val isBlocked: Boolean = false, // Whether this app is blocked by parent
    
    val versionName: String? = null,
    
    val versionCode: Long = 0,
    
    val installedTimestamp: Long = 0,
    
    val firstDetectedAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis()
)
