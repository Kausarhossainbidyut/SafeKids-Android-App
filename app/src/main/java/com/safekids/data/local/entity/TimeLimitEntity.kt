package com.safekids.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Room Entity for Time Limits
 * Stores time limit configurations for apps
 */
@Entity(
    tableName = "time_limits",
    indices = [
        Index(value = ["packageName"], unique = true)
    ]
)
data class TimeLimitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    
    val packageName: String,
    
    val appName: String,
    
    val dailyLimitMillis: Long, // Daily time limit in milliseconds
    
    val weekdayLimitMillis: Long? = null, // Different limit for Mon-Fri (optional)
    
    val weekendLimitMillis: Long? = null, // Different limit for Sat-Sun (optional)
    
    val currentUsageMillis: Long = 0, // Current usage today
    
    val lastResetTimestamp: Long = System.currentTimeMillis(), // Last time limit was reset
    
    val warningThresholdPercent: Int = 80, // Show warning at this % (default 80%)
    
    val isWarningShown: Boolean = false, // Whether warning notification was shown today
    
    val isLimitExceeded: Boolean = false, // Whether limit is currently exceeded
    
    val isEnabled: Boolean = true, // Whether time limit is active
    
    val createdAt: Long = System.currentTimeMillis(),
    
    val updatedAt: Long = System.currentTimeMillis()
)
