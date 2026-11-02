package com.safekids.domain.model

/**
 * Domain model for Time Limit
 * Represents time limit configuration for an app
 */
data class TimeLimit(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val dailyLimitMillis: Long,
    val weekdayLimitMillis: Long? = null,
    val weekendLimitMillis: Long? = null,
    val currentUsageMillis: Long = 0,
    val lastResetTimestamp: Long = System.currentTimeMillis(),
    val warningThresholdPercent: Int = 80,
    val isWarningShown: Boolean = false,
    val limitExceeded: Boolean = false, // Renamed from isLimitExceeded
    val isEnabled: Boolean = true
) {
    /**
     * Get current limit based on day of week
     */
    fun getCurrentLimit(): Long {
        val calendar = java.util.Calendar.getInstance()
        val dayOfWeek = calendar.get(java.util.Calendar.DAY_OF_WEEK)
        
        return when {
            dayOfWeek == java.util.Calendar.SATURDAY || dayOfWeek == java.util.Calendar.SUNDAY -> {
                weekendLimitMillis ?: dailyLimitMillis
            }
            else -> weekdayLimitMillis ?: dailyLimitMillis
        }
    }
    
    /**
     * Get remaining time in milliseconds
     */
    fun getRemainingTimeMillis(): Long {
        val limit = getCurrentLimit()
        return (limit - currentUsageMillis).coerceAtLeast(0)
    }
    
    /**
     * Get usage percentage
     */
    fun getUsagePercentage(): Int {
        val limit = getCurrentLimit()
        if (limit == 0L) return 0
        return ((currentUsageMillis.toDouble() / limit) * 100).toInt().coerceIn(0, 100)
    }
    
    /**
     * Check if warning should be shown
     */
    fun shouldShowWarning(): Boolean {
        return !isWarningShown && getUsagePercentage() >= warningThresholdPercent
    }
    
    /**
     * Check if limit is exceeded
     */
    fun isLimitExceeded(): Boolean {
        return currentUsageMillis >= getCurrentLimit()
    }
    
    /**
     * Get formatted remaining time (e.g., "30m")
     */
    fun getFormattedRemainingTime(): String {
        val remainingMillis = getRemainingTimeMillis()
        val hours = remainingMillis / (1000 * 60 * 60)
        val minutes = (remainingMillis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }
    
    /**
     * Get formatted limit time
     */
    fun getFormattedLimitTime(): String {
        val limitMillis = getCurrentLimit()
        val hours = limitMillis / (1000 * 60 * 60)
        val minutes = (limitMillis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }
}
