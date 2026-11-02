package com.safekids.util

import android.content.Context
import android.content.pm.ApplicationInfo
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import com.safekids.domain.model.AppCategory
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manager for getting installed apps information
 */
@Singleton
class InstalledAppsManager @Inject constructor(
    private val context: Context
) {
    private val packageManager: PackageManager = context.packageManager
    
    /**
     * Get all installed user apps (non-system)
     */
    fun getAllUserApps(): List<InstalledAppInfo> {
        return try {
            val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            
            packages
                .filter { !isSystemApp(it.applicationInfo) }
                .mapNotNull { packageInfo ->
                    try {
                        createAppInfo(packageInfo)
                    } catch (e: Exception) {
                        null
                    }
                }
                .sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get all installed apps (including system apps)
     */
    fun getAllApps(): List<InstalledAppInfo> {
        return try {
            val packages = packageManager.getInstalledPackages(PackageManager.GET_META_DATA)
            
            packages.mapNotNull { packageInfo ->
                try {
                    createAppInfo(packageInfo)
                } catch (e: Exception) {
                    null
                }
            }.sortedBy { it.appName.lowercase() }
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    /**
     * Get app info by package name
     */
    fun getAppInfo(packageName: String): InstalledAppInfo? {
        return try {
            val packageInfo = packageManager.getPackageInfo(packageName, PackageManager.GET_META_DATA)
            createAppInfo(packageInfo)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    /**
     * Get app name by package name
     */
    fun getAppName(packageName: String): String {
        return try {
            val applicationInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(applicationInfo).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            packageName
        }
    }
    
    /**
     * Get app icon by package name
     */
    fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: PackageManager.NameNotFoundException) {
            null
        }
    }
    
    /**
     * Check if app is installed
     */
    fun isAppInstalled(packageName: String): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }
    
    /**
     * Check if package is a system app
     */
    private fun isSystemApp(applicationInfo: ApplicationInfo): Boolean {
        return (applicationInfo.flags and ApplicationInfo.FLAG_SYSTEM) != 0 ||
                (applicationInfo.flags and ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0
    }
    
    /**
     * Create InstalledAppInfo from PackageInfo
     */
    private fun createAppInfo(packageInfo: PackageInfo): InstalledAppInfo {
        val applicationInfo = packageInfo.applicationInfo
        val appName = packageManager.getApplicationLabel(applicationInfo).toString()
        val category = detectCategory(packageInfo.packageName, appName)
        
        return InstalledAppInfo(
            packageName = packageInfo.packageName,
            appName = appName,
            category = category,
            isSystemApp = isSystemApp(applicationInfo),
            versionName = packageInfo.versionName ?: "Unknown",
            versionCode = packageInfo.longVersionCode,
            installedTimestamp = packageInfo.firstInstallTime
        )
    }
    
    /**
     * Detect app category based on package name and app name
     */
    private fun detectCategory(packageName: String, appName: String): AppCategory {
        val lowerPackage = packageName.lowercase()
        val lowerName = appName.lowercase()
        
        return when {
            // Games
            lowerPackage.contains("game") || 
            lowerPackage.contains("play.games") ||
            lowerName.contains("game") -> AppCategory.GAME
            
            // Social Media
            lowerPackage.contains("facebook") ||
            lowerPackage.contains("instagram") ||
            lowerPackage.contains("twitter") ||
            lowerPackage.contains("tiktok") ||
            lowerPackage.contains("snapchat") ||
            lowerPackage.contains("social") -> AppCategory.SOCIAL
            
            // Communication
            lowerPackage.contains("whatsapp") ||
            lowerPackage.contains("telegram") ||
            lowerPackage.contains("messenger") ||
            lowerPackage.contains("messaging") ||
            lowerPackage.contains("sms") ||
            lowerPackage.contains("phone") ||
            lowerPackage.contains("contacts") -> AppCategory.COMMUNICATION
            
            // Education
            lowerPackage.contains("edu") ||
            lowerPackage.contains("learn") ||
            lowerPackage.contains("study") ||
            lowerPackage.contains("school") ||
            lowerName.contains("education") -> AppCategory.EDUCATION
            
            // Entertainment
            lowerPackage.contains("youtube") ||
            lowerPackage.contains("netflix") ||
            lowerPackage.contains("video") ||
            lowerPackage.contains("movie") ||
            lowerPackage.contains("tv") -> AppCategory.ENTERTAINMENT
            
            // Shopping
            lowerPackage.contains("shop") ||
            lowerPackage.contains("amazon") ||
            lowerPackage.contains("ebay") ||
            lowerPackage.contains("store") -> AppCategory.SHOPPING
            
            // News
            lowerPackage.contains("news") ||
            lowerPackage.contains("magazine") ||
            lowerPackage.contains("reader") -> AppCategory.NEWS
            
            // Photo & Video
            lowerPackage.contains("camera") ||
            lowerPackage.contains("photo") ||
            lowerPackage.contains("gallery") -> AppCategory.PHOTO_VIDEO
            
            // Music & Audio
            lowerPackage.contains("music") ||
            lowerPackage.contains("audio") ||
            lowerPackage.contains("spotify") ||
            lowerPackage.contains("radio") -> AppCategory.MUSIC_AUDIO
            
            // Productivity
            lowerPackage.contains("office") ||
            lowerPackage.contains("docs") ||
            lowerPackage.contains("note") ||
            lowerPackage.contains("calendar") ||
            lowerPackage.contains("productivity") -> AppCategory.PRODUCTIVITY
            
            // Health & Fitness
            lowerPackage.contains("health") ||
            lowerPackage.contains("fitness") ||
            lowerPackage.contains("medical") ||
            lowerPackage.contains("workout") -> AppCategory.HEALTH_FITNESS
            
            // Tools
            lowerPackage.contains("tool") ||
            lowerPackage.contains("util") ||
            lowerPackage.contains("settings") -> AppCategory.TOOLS
            
            else -> AppCategory.OTHER
        }
    }
    
    /**
     * Convert Drawable to Bitmap
     */
    fun drawableToBitmap(drawable: Drawable): Bitmap {
        if (drawable is BitmapDrawable) {
            return drawable.bitmap
        }
        
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.width, canvas.height)
        drawable.draw(canvas)
        
        return bitmap
    }
}

/**
 * Data class for installed app information
 */
data class InstalledAppInfo(
    val packageName: String,
    val appName: String,
    val category: AppCategory,
    val isSystemApp: Boolean,
    val versionName: String,
    val versionCode: Long,
    val installedTimestamp: Long
)
