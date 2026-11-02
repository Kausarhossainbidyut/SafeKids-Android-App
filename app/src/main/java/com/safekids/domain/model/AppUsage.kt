package com.safekids.domain.model

/**
 * Domain model for App Usage
 * Represents daily usage data for an app
 */
data class AppUsage(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val usageTimeMillis: Long,
    val date: String, // Format: "yyyy-MM-dd"
    val launchCount: Int = 0,
    val lastUsedTimestamp: Long = System.currentTimeMillis()
) {
    /**
     * Get usage time in hours and minutes (e.g., "2h 30m")
     */
    fun getFormattedUsageTime(): String {
        val hours = usageTimeMillis / (1000 * 60 * 60)
        val minutes = (usageTimeMillis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
    
    /**
     * Get usage percentage of total time
     */
    fun getUsagePercentage(totalUsageMillis: Long): Int {
        if (totalUsageMillis == 0L) return 0
        return ((usageTimeMillis.toDouble() / totalUsageMillis) * 100).toInt()
    }
}
