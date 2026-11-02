package com.safekids.domain.repository

import com.safekids.domain.model.AppUsage
import com.safekids.domain.model.UsageStatistics
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for usage statistics operations
 */
interface UsageStatsRepository {
    
    /**
     * Get usage stats for today
     */
    suspend fun getTodayUsage(): List<AppUsage>
    
    /**
     * Get usage stats for a specific date
     */
    suspend fun getUsageForDate(date: String): List<AppUsage>
    
    /**
     * Get usage stats for date range
     */
    suspend fun getUsageForDateRange(startDate: String, endDate: String): List<AppUsage>
    
    /**
     * Get usage for specific app today
     */
    suspend fun getTodayUsageForApp(packageName: String): Long
    
    /**
     * Get total screen time for today
     */
    suspend fun getTodayTotalScreenTime(): Long
    
    /**
     * Get usage statistics for date range
     */
    suspend fun getUsageStatistics(startDate: String, endDate: String): UsageStatistics
    
    /**
     * Sync usage stats from system to database
     */
    suspend fun syncUsageStats()
    
    /**
     * Observe today's usage (reactive)
     */
    fun observeTodayUsage(): Flow<List<AppUsage>>
    
    /**
     * Get top used apps for today
     */
    suspend fun getTopUsedApps(limit: Int = 10): List<AppUsage>
    
    /**
     * Delete old usage data (older than specified days)
     */
    suspend fun deleteOldUsageData(daysToKeep: Int = 90)
}
