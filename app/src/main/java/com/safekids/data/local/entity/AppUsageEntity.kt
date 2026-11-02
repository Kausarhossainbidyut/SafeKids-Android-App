package com.safekids.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for App Usage tracking
 * Stores daily usage data for each app
 */
@Entity(
    tableName = "app_usage",
    indices = [
        Index(value = ["packageName", "date"], unique = true)
    ]
)
data class AppUsageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val packageName: String,
    
    val appName: String,
    
    val usageTimeMillis: Long, // Total usage time in milliseconds for this date
    
    val date: String, // Format: "yyyy-MM-dd" (e.g., "2026-11-02")
    
    val launchCount: Int = 0, // Number of times app was launched
    
    val lastUsedTimestamp: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis()
)
