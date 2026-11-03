package com.safekids.data.repository

import com.safekids.data.local.dao.InstalledAppDao
import com.safekids.data.local.entity.InstalledAppEntity
import com.safekids.data.mapper.toDomain
import com.safekids.data.mapper.toDomainList
import com.safekids.domain.model.InstalledApp
import com.safekids.domain.repository.AppManagementRepository
import com.safekids.util.InstalledAppsManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppManagementRepositoryImpl @Inject constructor(
    private val installedAppDao: InstalledAppDao,
    private val installedAppsManager: InstalledAppsManager
) : AppManagementRepository {
    
    override suspend fun getAllUserApps(): List<InstalledApp> {
        return installedAppDao.getAllUserAppsSync().toDomainList()
    }
    
    override fun observeAllUserApps(): Flow<List<InstalledApp>> {
        return installedAppDao.getAllUserApps().map { it.toDomainList() }
    }
    
    override suspend fun getAppByPackage(packageName: String): InstalledApp? {
        return installedAppDao.getAppByPackage(packageName)?.toDomain()
    }
    
    override suspend fun getAppsByCategory(category: String): List<InstalledApp> {
        return installedAppDao.getAppsByCategory(category).toDomainList()
    }
    
    override suspend fun getBlockedApps(): List<InstalledApp> {
        return installedAppDao.getBlockedApps().toDomainList()
    }
    
    override fun observeBlockedApps(): Flow<List<InstalledApp>> {
        return installedAppDao.observeBlockedApps().map { it.toDomainList() }
    }
    
    override suspend fun blockApp(packageName: String): Result<Unit> {
        return try {
            installedAppDao.updateBlockStatus(packageName, true)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun unblockApp(packageName: String): Result<Unit> {
        return try {
            installedAppDao.updateBlockStatus(packageName, false)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun isAppBlocked(packageName: String): Boolean {
        val app = installedAppDao.getAppByPackage(packageName)
        return app?.isBlocked == true
    }
    
    override suspend fun syncInstalledApps() {
        try {
            android.util.Log.d("AppManagementRepo", "Starting syncInstalledApps...")
            val deviceApps = installedAppsManager.getAllUserApps()
            android.util.Log.d("AppManagementRepo", "Found ${deviceApps.size} apps on device")
            
            val currentPackages = deviceApps.map { it.packageName }
            
            // Remove uninstalled apps from database
            installedAppDao.deleteUninstalledApps(currentPackages)
            
            // Insert or update apps in database
            val entities = deviceApps.map { appInfo ->
                val existing = installedAppDao.getAppByPackage(appInfo.packageName)
                
                if (existing != null) {
                    // Update existing app
                    existing.copy(
                        appName = appInfo.appName,
                        category = appInfo.category.name,
                        versionName = appInfo.versionName,
                        versionCode = appInfo.versionCode,
                        updatedAt = System.currentTimeMillis()
                    )
                } else {
                    // Create new app entry
                    InstalledAppEntity(
                        packageName = appInfo.packageName,
                        appName = appInfo.appName,
                        category = appInfo.category.name,
                        isSystemApp = appInfo.isSystemApp,
                        versionName = appInfo.versionName,
                        versionCode = appInfo.versionCode,
                        installedTimestamp = appInfo.installedTimestamp
                    )
                }
            }
            
            android.util.Log.d("AppManagementRepo", "Inserting ${entities.size} apps to database")
            installedAppDao.insertAll(entities)
            
            // Verify insertion
            val count = installedAppDao.getUserAppsCount()
            android.util.Log.d("AppManagementRepo", "Sync complete. Database has $count apps")
        } catch (e: Exception) {
            android.util.Log.e("AppManagementRepo", "Sync failed: ${e.message}", e)
        }
    }
    
    override suspend fun updateAppCategory(packageName: String, category: String): Result<Unit> {
        return try {
            installedAppDao.updateCategory(packageName, category)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    override suspend fun getUserAppsCount(): Int {
        return installedAppDao.getUserAppsCount()
    }
    
    override suspend fun getBlockedAppsCount(): Int {
        return installedAppDao.getBlockedAppsCount()
    }
}
