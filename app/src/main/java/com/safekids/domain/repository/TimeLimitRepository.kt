package com.safekids.domain.repository

import com.safekids.domain.model.BlockLog
import com.safekids.domain.model.TimeLimit
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for time limit operations
 */
interface TimeLimitRepository {
    
    /**
     * Get all active time limits
     */
    suspend fun getAllActiveLimits(): List<TimeLimit>
    
    /**
     * Observe all active limits (reactive)
     */
    fun observeAllActiveLimits(): Flow<List<TimeLimit>>
    
    /**
     * Get time limit for specific app
     */
    suspend fun getLimitForApp(packageName: String): TimeLimit?
    
    /**
     * Observe time limit for app (reactive)
     */
    fun observeLimitForApp(packageName: String): Flow<TimeLimit?>
    
    /**
     * Set time limit for app
     */
    suspend fun setTimeLimit(
        packageName: String,
        appName: String,
        dailyLimitMillis: Long,
        weekdayLimitMillis: Long? = null,
        weekendLimitMillis: Long? = null
    ): Result<Unit>
    
    /**
     * Update current usage for app
     */
    suspend fun updateCurrentUsage(packageName: String, usageMillis: Long)
    
    /**
     * Reset daily usage for all apps (called at midnight)
     */
    suspend fun resetAllDailyUsage()
    
    /**
     * Reset usage for specific app
     */
    suspend fun resetAppUsage(packageName: String)
    
    /**
     * Get apps that exceeded time limit
     */
    suspend fun getExceededLimits(): List<TimeLimit>
    
    /**
     * Check if app limit is exceeded
     */
    suspend fun isLimitExceeded(packageName: String): Boolean
    
    /**
     * Mark warning as shown for app
     */
    suspend fun markWarningShown(packageName: String)
    
    /**
     * Update limit exceeded status
     */
    suspend fun updateLimitExceeded(packageName: String, exceeded: Boolean)
    
    /**
     * Remove time limit for app
     */
    suspend fun removeTimeLimit(packageName: String): Result<Unit>
    
    /**
     * Get block logs
     */
    suspend fun getBlockLogs(limit: Int = 50): List<BlockLog>
    
    /**
     * Get block logs for app
     */
    suspend fun getBlockLogsForApp(packageName: String): List<BlockLog>
    
    /**
     * Add block log
     */
    suspend fun addBlockLog(
        packageName: String,
        appName: String,
        reason: String
    ): Result<Long>
    
    /**
     * Update unblock log
     */
    suspend fun updateUnblockLog(
        logId: Long,
        reason: String,
        byParent: Boolean = false
    ): Result<Unit>
    
    /**
     * Get currently blocked apps from logs
     */
    suspend fun getCurrentlyBlockedApps(): List<BlockLog>
}
