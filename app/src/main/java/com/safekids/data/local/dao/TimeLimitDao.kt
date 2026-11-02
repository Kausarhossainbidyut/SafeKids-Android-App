package com.safekids.data.local.dao

import androidx.room.*
import com.safekids.data.local.entity.TimeLimitEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Time Limits
 */
@Dao
interface TimeLimitDao {
    
    @Query("SELECT * FROM time_limits WHERE isEnabled = 1")
    fun getAllActiveLimits(): Flow<List<TimeLimitEntity>>
    
    @Query("SELECT * FROM time_limits")
    suspend fun getAllLimitsSync(): List<TimeLimitEntity>
    
    @Query("SELECT * FROM time_limits WHERE packageName = :packageName LIMIT 1")
    suspend fun getLimitByPackage(packageName: String): TimeLimitEntity?
    
    @Query("SELECT * FROM time_limits WHERE packageName = :packageName LIMIT 1")
    fun observeLimitByPackage(packageName: String): Flow<TimeLimitEntity?>
    
    @Query("SELECT * FROM time_limits WHERE isLimitExceeded = 1 AND isEnabled = 1")
    suspend fun getExceededLimits(): List<TimeLimitEntity>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(timeLimit: TimeLimitEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(timeLimits: List<TimeLimitEntity>)
    
    @Update
    suspend fun update(timeLimit: TimeLimitEntity)
    
    @Query("UPDATE time_limits SET currentUsageMillis = :usageMillis, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateCurrentUsage(packageName: String, usageMillis: Long, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE time_limits SET isLimitExceeded = :exceeded, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateLimitExceeded(packageName: String, exceeded: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE time_limits SET isWarningShown = :shown, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateWarningShown(packageName: String, shown: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE time_limits SET currentUsageMillis = 0, isWarningShown = 0, isLimitExceeded = 0, lastResetTimestamp = :timestamp, updatedAt = :timestamp WHERE isEnabled = 1")
    suspend fun resetAllDailyUsage(timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE time_limits SET currentUsageMillis = 0, isWarningShown = 0, isLimitExceeded = 0, lastResetTimestamp = :timestamp, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun resetPackageUsage(packageName: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun delete(timeLimit: TimeLimitEntity)
    
    @Query("DELETE FROM time_limits WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
    
    @Query("DELETE FROM time_limits")
    suspend fun deleteAll()
}
