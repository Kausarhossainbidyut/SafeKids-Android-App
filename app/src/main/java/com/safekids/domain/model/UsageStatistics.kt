package com.safekids.domain.model

/**
 * Domain model for Usage Statistics
 * Aggregated usage data for a time period
 */
data class UsageStatistics(
    val totalScreenTimeMillis: Long,
    val topApps: List<AppUsage>,
    val dailyUsage: Map<String, Long>, // Date to total usage mapping
    val categoryBreakdown: Map<AppCategory, Long>,
    val mostUsedApp: AppUsage?,
    val averageDailyUsageMillis: Long,
    val startDate: String,
    val endDate: String
) {
    /**
     * Get formatted total screen time
     */
    fun getFormattedTotalScreenTime(): String {
        val hours = totalScreenTimeMillis / (1000 * 60 * 60)
        val minutes = (totalScreenTimeMillis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
    
    /**
     * Get formatted average daily usage
     */
    fun getFormattedAverageDailyUsage(): String {
        val hours = averageDailyUsageMillis / (1000 * 60 * 60)
        val minutes = (averageDailyUsageMillis % (1000 * 60 * 60)) / (1000 * 60)
        
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "< 1m"
        }
    }
    
    companion object {
        /**
         * Create empty statistics
         */
        fun empty(): UsageStatistics {
            val today = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.US)
                .format(java.util.Date())
            
            return UsageStatistics(
                totalScreenTimeMillis = 0,
                topApps = emptyList(),
                dailyUsage = emptyMap(),
                categoryBreakdown = emptyMap(),
                mostUsedApp = null,
                averageDailyUsageMillis = 0,
                startDate = today,
                endDate = today
            )
        }
    }
}

/**
 * Block Log domain model
 */
data class BlockLog(
    val id: Long = 0,
    val packageName: String,
    val appName: String,
    val blockTimestamp: Long,
    val unblockTimestamp: Long? = null,
    val blockReason: BlockReason,
    val unblockReason: UnblockReason? = null,
    val unlockedByParent: Boolean = false
)

/**
 * Block reasons
 */
enum class BlockReason(val displayName: String) {
    TIME_LIMIT_EXCEEDED("Time Limit Exceeded"),
    MANUAL_BLOCK("Manually Blocked"),
    SCHEDULE_BLOCK("Scheduled Block");
    
    companion object {
        fun fromString(value: String): BlockReason {
            return entries.find { it.name == value } ?: MANUAL_BLOCK
        }
    }
}

/**
 * Unblock reasons
 */
enum class UnblockReason(val displayName: String) {
    PARENT_OVERRIDE("Parent Override"),
    TIME_LIMIT_RESET("Time Limit Reset"),
    MANUAL_UNBLOCK("Manually Unblocked");
    
    companion object {
        fun fromString(value: String): UnblockReason {
            return entries.find { it.name == value } ?: MANUAL_UNBLOCK
        }
    }
}
