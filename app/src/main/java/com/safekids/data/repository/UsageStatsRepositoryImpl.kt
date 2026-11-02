package com.safekids.data.repository

import com.safekids.data.local.dao.AppUsageDao
import com.safekids.data.mapper.toDomain
import com.safekids.data.mapper.toDomainList
import com.safekids.domain.model.AppCategory
import com.safekids.domain.model.AppUsage
import com.safekids.domain.model.UsageStatistics
import com.safekids.domain.repository.UsageStatsRepository
import com.safekids.util.UsageStatsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsageStatsRepositoryImpl @Inject constructor(
    private val appUsageDao: AppUsageDao,
    private val usageStatsManager: UsageStatsManager
) : UsageStatsRepository {
    
    private val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.US)
    
    override suspend fun getTodayUsage(): List<AppUsage> {
        val today = usageStatsManager.getTodayDate()
        return appUsageDao.getUsageByDateSync(today).toDomainList()
    }
    
    override suspend fun getUsageForDate(date: String): List<AppUsage> {
        return appUsageDao.getUsageByDateSync(date).toDomainList()
    }
    
    override suspend fun getUsageForDateRange(startDate: String, endDate: String): List<AppUsage> {
        return appUsageDao.getUsageInDateRange(startDate, endDate).toDomainList()
    }
    
    override suspend fun getTodayUsageForApp(packageName: String): Long {
        return usageStatsManager.getTodayUsageForApp(packageName)
    }
    
    override suspend fun getTodayTotalScreenTime(): Long {
        val today = usageStatsManager.getTodayDate()
        return appUsageDao.getTotalUsageForDate(today) ?: 0L
    }
    
    override suspend fun getUsageStatistics(startDate: String, endDate: String): UsageStatistics {
        val usageList = getUsageForDateRange(startDate, endDate)
        
        if (usageList.isEmpty()) {
            return UsageStatistics.empty()
        }
        
        // Calculate total screen time
        val totalScreenTime = usageList.sumOf { it.usageTimeMillis }
        
        // Get top apps
        val topApps = usageList
            .groupBy { it.packageName }
            .map { (_, apps) -> 
                apps.first().copy(
                    usageTimeMillis = apps.sumOf { it.usageTimeMillis },
                    launchCount = apps.sumOf { it.launchCount }
                )
            }
            .sortedByDescending { it.usageTimeMillis }
            .take(10)
        
        // Get daily usage map
        val dailyUsage = usageList
            .groupBy { it.date }
            .mapValues { (_, apps) -> apps.sumOf { it.usageTimeMillis } }
        
        // Calculate average daily usage
        val averageDaily = if (dailyUsage.isNotEmpty()) {
            totalScreenTime / dailyUsage.size
        } else {
            0L
        }
        
        // Get most used app
        val mostUsedApp = topApps.firstOrNull()
        
        return UsageStatistics(
            totalScreenTimeMillis = totalScreenTime,
            topApps = topApps,
            dailyUsage = dailyUsage,
            categoryBreakdown = emptyMap(), // TODO: Implement with app categories
            mostUsedApp = mostUsedApp,
            averageDailyUsageMillis = averageDaily,
            startDate = startDate,
            endDate = endDate
        )
    }
    
    override suspend fun syncUsageStats() {
        try {
            val usageDataList = usageStatsManager.getTodayUsageStats()
            val today = usageStatsManager.getTodayDate()
            
            usageDataList.forEach { usageData ->
                val existing = appUsageDao.getUsageByPackageAndDate(usageData.packageName, today)
                
                if (existing != null) {
                    // Update existing
                    appUsageDao.updateUsage(
                        packageName = usageData.packageName,
                        date = today,
                        usageMillis = usageData.usageTimeMillis,
                        launchCount = usageData.launchCount,
                        timestamp = usageData.lastTimeUsed
                    )
                } else {
                    // Insert new
                    appUsageDao.insert(
                        com.safekids.data.local.entity.AppUsageEntity(
                            packageName = usageData.packageName,
                            appName = usageData.appName,
                            usageTimeMillis = usageData.usageTimeMillis,
                            date = today,
                            launchCount = usageData.launchCount,
                            lastUsedTimestamp = usageData.lastTimeUsed
                        )
                    )
                }
            }
        } catch (e: Exception) {
            // Handle error silently or log
        }
    }
    
    override fun observeTodayUsage(): Flow<List<AppUsage>> {
        val today = usageStatsManager.getTodayDate()
        return appUsageDao.getUsageByDate(today).map { it.toDomainList() }
    }
    
    override suspend fun getTopUsedApps(limit: Int): List<AppUsage> {
        val today = usageStatsManager.getTodayDate()
        return appUsageDao.getTopUsedApps(today, limit).toDomainList()
    }
    
    override suspend fun deleteOldUsageData(daysToKeep: Int) {
        try {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, -daysToKeep)
            val cutoffDate = dateFormat.format(calendar.time)
            
            appUsageDao.deleteOlderThan(cutoffDate)
        } catch (e: Exception) {
            // Handle error
        }
    }
}
