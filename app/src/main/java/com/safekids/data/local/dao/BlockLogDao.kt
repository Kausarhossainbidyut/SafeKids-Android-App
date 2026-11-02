package com.safekids.data.local.dao

import androidx.room.*
import com.safekids.data.local.entity.BlockLogEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Block Logs
 */
@Dao
interface BlockLogDao {
    
    @Query("SELECT * FROM block_logs ORDER BY blockTimestamp DESC")
    fun getAllLogs(): Flow<List<BlockLogEntity>>
    
    @Query("SELECT * FROM block_logs ORDER BY blockTimestamp DESC LIMIT :limit")
    suspend fun getRecentLogs(limit: Int = 50): List<BlockLogEntity>
    
    @Query("SELECT * FROM block_logs WHERE packageName = :packageName ORDER BY blockTimestamp DESC")
    suspend fun getLogsByPackage(packageName: String): List<BlockLogEntity>
    
    @Query("SELECT * FROM block_logs WHERE blockTimestamp >= :startTime AND blockTimestamp <= :endTime ORDER BY blockTimestamp DESC")
    suspend fun getLogsInTimeRange(startTime: Long, endTime: Long): List<BlockLogEntity>
    
    @Query("SELECT * FROM block_logs WHERE unblockTimestamp IS NULL ORDER BY blockTimestamp DESC")
    suspend fun getCurrentlyBlockedApps(): List<BlockLogEntity>
    
    @Query("SELECT * FROM block_logs WHERE packageName = :packageName AND unblockTimestamp IS NULL LIMIT 1")
    suspend fun getActiveBlockLog(packageName: String): BlockLogEntity?
    
    @Query("SELECT COUNT(*) FROM block_logs WHERE blockTimestamp >= :startTime AND blockTimestamp <= :endTime")
    suspend fun getBlockCountInRange(startTime: Long, endTime: Long): Int
    
    @Query("SELECT COUNT(*) FROM block_logs WHERE packageName = :packageName")
    suspend fun getBlockCountByPackage(packageName: String): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(log: BlockLogEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(logs: List<BlockLogEntity>)
    
    @Update
    suspend fun update(log: BlockLogEntity)
    
    @Query("UPDATE block_logs SET unblockTimestamp = :timestamp, unblockReason = :reason, unlockedByParent = :byParent WHERE id = :logId")
    suspend fun updateUnblock(logId: Long, timestamp: Long, reason: String, byParent: Boolean = false)
    
    @Query("UPDATE block_logs SET unblockTimestamp = :timestamp, unblockReason = :reason WHERE packageName = :packageName AND unblockTimestamp IS NULL")
    suspend fun unblockPackage(packageName: String, timestamp: Long, reason: String)
    
    @Delete
    suspend fun delete(log: BlockLogEntity)
    
    @Query("DELETE FROM block_logs WHERE blockTimestamp < :beforeTime")
    suspend fun deleteOlderThan(beforeTime: Long)
    
    @Query("DELETE FROM block_logs")
    suspend fun deleteAll()
}
