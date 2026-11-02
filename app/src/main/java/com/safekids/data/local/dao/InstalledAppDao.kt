package com.safekids.data.local.dao

import androidx.room.*
import com.safekids.data.local.entity.InstalledAppEntity
import kotlinx.coroutines.flow.Flow

/**
 * DAO for Installed Apps
 */
@Dao
interface InstalledAppDao {
    
    @Query("SELECT * FROM installed_apps WHERE isSystemApp = 0 ORDER BY appName ASC")
    fun getAllUserApps(): Flow<List<InstalledAppEntity>>
    
    @Query("SELECT * FROM installed_apps WHERE isSystemApp = 0 ORDER BY appName ASC")
    suspend fun getAllUserAppsSync(): List<InstalledAppEntity>
    
    @Query("SELECT * FROM installed_apps ORDER BY appName ASC")
    suspend fun getAllApps(): List<InstalledAppEntity>
    
    @Query("SELECT * FROM installed_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getAppByPackage(packageName: String): InstalledAppEntity?
    
    @Query("SELECT * FROM installed_apps WHERE packageName = :packageName LIMIT 1")
    fun observeAppByPackage(packageName: String): Flow<InstalledAppEntity?>
    
    @Query("SELECT * FROM installed_apps WHERE category = :category AND isSystemApp = 0 ORDER BY appName ASC")
    suspend fun getAppsByCategory(category: String): List<InstalledAppEntity>
    
    @Query("SELECT * FROM installed_apps WHERE isBlocked = 1 AND isSystemApp = 0")
    suspend fun getBlockedApps(): List<InstalledAppEntity>
    
    @Query("SELECT * FROM installed_apps WHERE isBlocked = 1 AND isSystemApp = 0")
    fun observeBlockedApps(): Flow<List<InstalledAppEntity>>
    
    @Query("SELECT COUNT(*) FROM installed_apps WHERE isSystemApp = 0")
    suspend fun getUserAppsCount(): Int
    
    @Query("SELECT COUNT(*) FROM installed_apps WHERE isBlocked = 1 AND isSystemApp = 0")
    suspend fun getBlockedAppsCount(): Int
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(app: InstalledAppEntity): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(apps: List<InstalledAppEntity>)
    
    @Update
    suspend fun update(app: InstalledAppEntity)
    
    @Query("UPDATE installed_apps SET isBlocked = :blocked, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateBlockStatus(packageName: String, blocked: Boolean, timestamp: Long = System.currentTimeMillis())
    
    @Query("UPDATE installed_apps SET category = :category, updatedAt = :timestamp WHERE packageName = :packageName")
    suspend fun updateCategory(packageName: String, category: String, timestamp: Long = System.currentTimeMillis())
    
    @Delete
    suspend fun delete(app: InstalledAppEntity)
    
    @Query("DELETE FROM installed_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)
    
    @Query("DELETE FROM installed_apps WHERE packageName NOT IN (:currentPackages)")
    suspend fun deleteUninstalledApps(currentPackages: List<String>)
    
    @Query("DELETE FROM installed_apps")
    suspend fun deleteAll()
}
