package com.safekids.data.repository

import com.safekids.data.local.dao.BlockLogDao
import com.safekids.data.local.dao.TimeLimitDao
import com.safekids.data.local.entity.BlockLogEntity
import com.safekids.data.local.entity.TimeLimitEntity
import com.safekids.data.mapper.toDomain
import com.safekids.data.mapper.toDomainList
import com.safekids.domain.model.BlockLog
import com.safekids.domain.model.TimeLimit
import com.safekids.domain.repository.TimeLimitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TimeLimitRepositoryImpl @Inject constructor(
    private val timeLimitDao: TimeLimitDao,
    private val blockLogDao: BlockLogDao
) : TimeLimitRepository {
    
    override suspend fun getAllActiveLimits(): List<TimeLimit> {
        return timeLimitDao.getAllLimitsSync().toDomainList()
    }
    
    override fun observeAllActiveLimits(): Flow<List<TimeLimit>> {
        return timeLimitDao.getAllActiveLimits().map { it.toDomainList() }
    }
    
    override suspend fun getLimitForApp(packageName: String): TimeLimit? {
        return timeLimitDao.getLimitByPackage(packageName)?.toDomain()
    }
    
    override fun observeLimitForApp(packageName: String): Flow<TimeLimit?> {
        return timeLimitDao.observeLimitByPackage(packageName).map { it?.toDomain() }
    }
    
    override suspend fun setTimeLimit(
        packageName: String,
        appName: String,
        dailyLimitMillis: Long,
        weekdayLimitMillis: Long?,
        weekendLimitMillis: Long?
    ): Result<Unit> {
        return try {
            val existing = timeLimitDao.getLimitByPackage(packageName)
            
            if (existing != null) {
                // Update existing limit
                val updated = existing.copy(
                    appName = appName,
                    dailyLimitMillis = dailyLimitMillis,
                    weekdayLimitMillis = weekdayLimitMillis,
                    weekendLimitMillis = weekendLimitMillis,
                    isEnabled = true,
                    updatedAt = System.currentTimeMillis()
                )
                timeLimitDao.update(updated)
            } else {
                // Insert new limit
                val newLimit = TimeLimitEntity(
                    packageName = packageName,
                    appName = appName,
                    dailyLimitMillis = dailyLimitMillis,
                    weekdayLimitMillis = weekdayLimitMillis,
                    weekendLimitMillis = weekendLimitMillis,
                    isEnabled = true
                )
                timeLimitDao.insert(newLimit)
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateCurrentUsage(packageName: String, usageMillis: Long) {
        try {
            timeLimitDao.updateCurrentUsage(packageName, usageMillis)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun resetAllDailyUsage() {
        try {
            timeLimitDao.resetAllDailyUsage()
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun resetAppUsage(packageName: String) {
        try {
            timeLimitDao.resetPackageUsage(packageName)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun getExceededLimits(): List<TimeLimit> {
        return timeLimitDao.getExceededLimits().toDomainList()
    }
    
    override suspend fun isLimitExceeded(packageName: String): Boolean {
        val limit = timeLimitDao.getLimitByPackage(packageName) ?: return false
        return limit.isLimitExceeded
    }
    
    override suspend fun markWarningShown(packageName: String) {
        try {
            timeLimitDao.updateWarningShown(packageName, true)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun updateLimitExceeded(packageName: String, exceeded: Boolean) {
        try {
            timeLimitDao.updateLimitExceeded(packageName, exceeded)
        } catch (e: Exception) {
            // Handle error
        }
    }
    
    override suspend fun removeTimeLimit(packageName: String): Result<Unit> {
        return try {
            timeLimitDao.deleteByPackage(packageName)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getBlockLogs(limit: Int): List<BlockLog> {
        return blockLogDao.getRecentLogs(limit).toDomainList()
    }
    
    override suspend fun getBlockLogsForApp(packageName: String): List<BlockLog> {
        return blockLogDao.getLogsByPackage(packageName).toDomainList()
    }
    
    override suspend fun addBlockLog(
        packageName: String,
        appName: String,
        reason: String
    ): Result<Long> {
        return try {
            val log = BlockLogEntity(
                packageName = packageName,
                appName = appName,
                blockTimestamp = System.currentTimeMillis(),
                blockReason = reason
            )
            val id = blockLogDao.insert(log)
            Result.success(id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun updateUnblockLog(
        logId: Long,
        reason: String,
        byParent: Boolean
    ): Result<Unit> {
        return try {
            blockLogDao.updateUnblock(
                logId = logId,
                timestamp = System.currentTimeMillis(),
                reason = reason,
                byParent = byParent
            )
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getCurrentlyBlockedApps(): List<BlockLog> {
        return blockLogDao.getCurrentlyBlockedApps().toDomainList()
    }
}
