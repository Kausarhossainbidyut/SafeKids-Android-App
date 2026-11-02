package com.safekids.domain.repository

import com.safekids.domain.model.InstalledApp
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for app management operations
 */
interface AppManagementRepository {
    
    /**
     * Get all user-installed apps (non-system)
     */
    suspend fun getAllUserApps(): List<InstalledApp>
    
    /**
     * Observe all user apps (reactive)
     */
    fun observeAllUserApps(): Flow<List<InstalledApp>>
    
    /**
     * Get app by package name
     */
    suspend fun getAppByPackage(packageName: String): InstalledApp?
    
    /**
     * Get apps by category
     */
    suspend fun getAppsByCategory(category: String): List<InstalledApp>
    
    /**
     * Get all blocked apps
     */
    suspend fun getBlockedApps(): List<InstalledApp>
    
    /**
     * Observe blocked apps (reactive)
     */
    fun observeBlockedApps(): Flow<List<InstalledApp>>
    
    /**
     * Block an app
     */
    suspend fun blockApp(packageName: String): Result<Unit>
    
    /**
     * Unblock an app
     */
    suspend fun unblockApp(packageName: String): Result<Unit>
    
    /**
     * Check if app is blocked
     */
    suspend fun isAppBlocked(packageName: String): Boolean
    
    /**
     * Sync installed apps from device to database
     */
    suspend fun syncInstalledApps()
    
    /**
     * Update app category
     */
    suspend fun updateAppCategory(packageName: String, category: String): Result<Unit>
    
    /**
     * Get installed apps count
     */
    suspend fun getUserAppsCount(): Int
    
    /**
     * Get blocked apps count
     */
    suspend fun getBlockedAppsCount(): Int
}
