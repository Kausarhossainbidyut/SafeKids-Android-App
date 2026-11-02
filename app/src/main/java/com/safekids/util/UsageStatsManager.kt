package com.safekids.util

import android.app.usage.UsageStats
import android.app.usage.UsageStatsManager as AndroidUsageStatsManager
import android.content.Context
import android.content.pm.PackageManager
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Wrapper around Android UsageStatsManager
 * Provides easy access to app usage statistics
 */
@Singleton
class UsageStatsManager @Inject constructor(
    private val context: Context
) {
    private val usageStatsManager: AndroidUsageStatsManager? =
        context.getSystemService(Context.USAGE_STATS_SERVICE) as? AndroidUsageStatsManager
    
    private val packageManager: PackageManager = context.packageManager
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    /**
     * Get usage stats for today
     */
    fun getTodayUsageStats(): List<AppUsageData> {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        return getUsageStats(startTime, endTime)
    }
    
    /**
     * Get usage stats for a specific date
     */
    fun getUsageStatsForDate(date: String): List<AppUsageData> {
        try {
            val dateObj = dateFormat.parse(date) ?: return emptyList()
            
            val calendar = Calendar.getInstance()
            calendar.time = dateObj
            calendar.set(Calendar.HOUR_OF_DAY, 0)
            calendar.set(Calendar.MINUTE, 0)
            calendar.set(Calendar.SECOND, 0)
            calendar.set(Calendar.MILLISECOND, 0)
            
            val startTime = calendar.timeInMillis
            
            calendar.set(Calendar.HOUR_OF_DAY, 23)
            calendar.set(Calendar.MINUTE, 59)
            calendar.set(Calendar.SECOND, 59)
            calendar.set(Calendar.MILLISECOND, 999)
            
            val endTime = calendar.timeInMillis
            
            return getUsageStats(startTime, endTime)
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    /**
     * Get usage stats for a time range
     */
    fun getUsageStats(startTime: Long, endTime: Long): List<AppUsageData> {
        if (usageStatsManager == null) return emptyList()
        
        try {
            val usageStatsList = usageStatsManager.queryUsageStats(
                AndroidUsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )
            
            return usageStatsList
                .filter { it.totalTimeInForeground > 0 }
                .mapNotNull { usageStats ->
                    try {
                        val appInfo = packageManager.getApplicationInfo(usageStats.packageName, 0)
                        val appName = packageManager.getApplicationLabel(appInfo).toString()
                        
                        AppUsageData(
                            packageName = usageStats.packageName,
                            appName = appName,
                            usageTimeMillis = usageStats.totalTimeInForeground,
                            lastTimeUsed = usageStats.lastTimeUsed,
                            firstTimeStamp = usageStats.firstTimeStamp,
                            lastTimeStamp = usageStats.lastTimeStamp,
                            launchCount = getLaunchCount(usageStats)
                        )
                    } catch (e: PackageManager.NameNotFoundException) {
                        null
                    }
                }
                .sortedByDescending { it.usageTimeMillis }
        } catch (e: Exception) {
            return emptyList()
        }
    }
    
    /**
     * Get usage for specific app today
     */
    fun getTodayUsageForApp(packageName: String): Long {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        
        val startTime = calendar.timeInMillis
        val endTime = System.currentTimeMillis()
        
        return getUsageForApp(packageName, startTime, endTime)
    }
    
    /**
     * Get usage for specific app in time range
     */
    fun getUsageForApp(packageName: String, startTime: Long, endTime: Long): Long {
        if (usageStatsManager == null) return 0L
        
        try {
            val usageStatsList = usageStatsManager.queryUsageStats(
                AndroidUsageStatsManager.INTERVAL_DAILY,
                startTime,
                endTime
            )
            
            return usageStatsList
                .filter { it.packageName == packageName }
                .sumOf { it.totalTimeInForeground }
        } catch (e: Exception) {
            return 0L
        }
    }
    
    /**
     * Get currently running app package name
     */
    fun getCurrentForegroundApp(): String? {
        if (usageStatsManager == null) return null
        
        try {
            val currentTime = System.currentTimeMillis()
            val usageStatsList = usageStatsManager.queryUsageStats(
                AndroidUsageStatsManager.INTERVAL_DAILY,
                currentTime - 1000 * 60, // Last minute
                currentTime
            )
            
            return usageStatsList
                .maxByOrNull { it.lastTimeUsed }
                ?.packageName
        } catch (e: Exception) {
            return null
        }
    }
    
    /**
     * Get total screen time for today
     */
    fun getTodayTotalScreenTime(): Long {
        return getTodayUsageStats().sumOf { it.usageTimeMillis }
    }
    
    /**
     * Get total screen time for date range
     */
    fun getTotalScreenTime(startDate: String, endDate: String): Long {
        try {
            val startDateObj = dateFormat.parse(startDate) ?: return 0L
            val endDateObj = dateFormat.parse(endDate) ?: return 0L
            
            val startCalendar = Calendar.getInstance()
            startCalendar.time = startDateObj
            startCalendar.set(Calendar.HOUR_OF_DAY, 0)
            startCalendar.set(Calendar.MINUTE, 0)
            startCalendar.set(Calendar.SECOND, 0)
            
            val endCalendar = Calendar.getInstance()
            endCalendar.time = endDateObj
            endCalendar.set(Calendar.HOUR_OF_DAY, 23)
            endCalendar.set(Calendar.MINUTE, 59)
            endCalendar.set(Calendar.SECOND, 59)
            
            return getUsageStats(startCalendar.timeInMillis, endCalendar.timeInMillis)
                .sumOf { it.usageTimeMillis }
        } catch (e: Exception) {
            return 0L
        }
    }
    
    /**
     * Get launch count from UsageStats
     */
    private fun getLaunchCount(usageStats: UsageStats): Int {
        return try {
            // Try to get launch count using reflection (may not work on all devices)
            val field = UsageStats::class.java.getDeclaredField("mLaunchCount")
            field.isAccessible = true
            field.getInt(usageStats)
        } catch (e: Exception) {
            0
        }
    }
    
    /**
     * Format today's date
     */
    fun getTodayDate(): String {
        return dateFormat.format(Date())
    }
    
    /**
     * Check if usage stats permission is granted
     */
    fun hasUsageStatsPermission(): Boolean {
        if (usageStatsManager == null) return false
        
        try {
            val currentTime = System.currentTimeMillis()
            val usageStatsList = usageStatsManager.queryUsageStats(
                AndroidUsageStatsManager.INTERVAL_DAILY,
                currentTime - 1000 * 60,
                currentTime
            )
            
            return usageStatsList.isNotEmpty()
        } catch (e: Exception) {
            return false
        }
    }
}

/**
 * Data class for app usage information
 */
data class AppUsageData(
    val packageName: String,
    val appName: String,
    val usageTimeMillis: Long,
    val lastTimeUsed: Long,
    val firstTimeStamp: Long = 0,
    val lastTimeStamp: Long = 0,
    val launchCount: Int = 0
)
