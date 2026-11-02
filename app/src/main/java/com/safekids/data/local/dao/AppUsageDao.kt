package com.safekids.data.local.dao

import androidx.room.*
import com.safekids.data.local.entity.AppUsageEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for App Usage tracking
 */
@Dao
interface AppUsageDao {
    
    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageTimeMillis DESC")
    fun getUsageByDate(date: String): Flow<List<AppUsageEntity>>
    
    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageTimeMillis DESC")
    suspend fun getUsageByDateSync(date: String): List<AppUsageEntity>
    
    @Query("SELECT * FROM app_usage WHERE packageName = :packageName AND date = :date LIMIT 1")
    suspend fun getUsageByPackageAndDate(packageName: String, date: String): AppUsageEntity?
    
    @Query("SELECT * FROM app_usage WHERE packageName = :packageName ORDER BY date DESC")
    suspend fun getUsageHistoryByPackage(packageName: String): List<AppUsageEntity>
    
    @Query("SELECT * FROM app_usage WHERE date >= :startDate AND date <= :endDate ORDER BY date DESC, usageTimeMillis DESC")
    suspend fun getUsageInDateRange(startDate: String, endDate: String): List<AppUsageEntity>
    
    @Query("SELECT * FROM app_usage WHERE date = :date ORDER BY usageTimeMillis DESC LIMIT :limit")
    suspend fun getTopUsedApps(date: String, limit: Int = 10): List<AppUsageEntity>
    
    @Query("SELECT SUM(usageTimeMillis) FROM app_usage WHERE date = :date")
    suspend fun getTotalUsageForDate(date: String): Long?
    
    @Query("SELECT SUM(usageTimeMillis) FROM app_usage WHERE packageName = :packageName AND date >= :startDate AND date <= :endDate")
    suspend fun getTotalUsageForPackageInRange(packageName: String, startDate: String, endDate: String): Long?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(appUsage: AppUsageEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(usages: List<AppUsageEntity>)
    
    @Update
    suspend fun update(appUsage: AppUsageEntity)
    
    @Query("UPDATE app_usage SET usageTimeMillis = :usageMillis, launchCount = :launchCount, lastUsedTimestamp = :timestamp, updatedAt = :updateTime WHERE packageName = :packageName AND date = :date")
    suspend fun updateUsage(
        packageName: String,
        date: String,
        usageMillis: Long,
        launchCount: Int,
        timestamp: Long,
        updateTime: Long = System.currentTimeMillis()
    )
    
    @Delete
    suspend fun delete(appUsage: AppUsageEntity)
    
    @Query("DELETE FROM app_usage WHERE date < :beforeDate")
    suspend fun deleteOlderThan(beforeDate: String)
    
    @Query("DELETE FROM app_usage")
    suspend fun deleteAll()
}
