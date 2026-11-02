package com.safekids.util

import android.app.AppOpsManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Process
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Helper class for checking and requesting permissions
 */
@Singleton
class PermissionHelper @Inject constructor(
    private val context: Context
) {
    
    /**
     * Check if Usage Stats permission is granted
     */
    fun hasUsageStatsPermission(): Boolean {
        return try {
            val appOps = context.getSystemService(Context.APP_OPS_SERVICE) as? AppOpsManager
            val mode = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                appOps?.unsafeCheckOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            } else {
                @Suppress("DEPRECATION")
                appOps?.checkOpNoThrow(
                    AppOpsManager.OPSTR_GET_USAGE_STATS,
                    Process.myUid(),
                    context.packageName
                )
            }
            mode == AppOpsManager.MODE_ALLOWED
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if Overlay permission is granted
     */
    fun hasOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else {
            true
        }
    }
    
    /**
     * Check if Notification permission is granted (Android 13+)
     */
    fun hasNotificationPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            NotificationManagerCompat.from(context).areNotificationsEnabled()
        } else {
            true // Not required before Android 13
        }
    }
    
    /**
     * Check if Accessibility Service is enabled
     */
    fun isAccessibilityServiceEnabled(): Boolean {
        return try {
            val settingValue = Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
            )
            
            val serviceName = "${context.packageName}/${context.packageName}.core.service.SafeKidsAccessibilityService"
            settingValue?.contains(serviceName) == true
        } catch (e: Exception) {
            false
        }
    }
    
    /**
     * Check if all required permissions are granted
     */
    fun hasAllRequiredPermissions(): Boolean {
        return hasUsageStatsPermission() && hasOverlayPermission() && hasNotificationPermission()
    }
    
    /**
     * Get list of missing permissions
     */
    fun getMissingPermissions(): List<PermissionType> {
        val missing = mutableListOf<PermissionType>()
        
        if (!hasUsageStatsPermission()) {
            missing.add(PermissionType.USAGE_STATS)
        }
        
        if (!hasOverlayPermission()) {
            missing.add(PermissionType.OVERLAY)
        }
        
        if (!hasNotificationPermission()) {
            missing.add(PermissionType.NOTIFICATION)
        }
        
        return missing
    }
    
    /**
     * Get intent to request Usage Stats permission
     */
    fun getUsageStatsPermissionIntent(): Intent {
        return Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)
    }
    
    /**
     * Get intent to request Overlay permission
     */
    fun getOverlayPermissionIntent(): Intent {
        return Intent(
            Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
            Uri.parse("package:${context.packageName}")
        )
    }
    
    /**
     * Get intent to request Notification permission
     */
    fun getNotificationPermissionIntent(): Intent {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
            }
        } else {
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts("package", context.packageName, null)
            }
        }
    }
    
    /**
     * Get intent to request Accessibility Service
     */
    fun getAccessibilitySettingsIntent(): Intent {
        return Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    }
    
    /**
     * Open app settings page
     */
    fun getAppSettingsIntent(): Intent {
        return Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", context.packageName, null)
        }
    }
}

/**
 * Types of permissions used in the app
 */
enum class PermissionType(val displayName: String, val description: String) {
    USAGE_STATS(
        "Usage Access",
        "Required to monitor app usage and enforce time limits"
    ),
    OVERLAY(
        "Draw Over Apps",
        "Required to show lock screen when time limit is reached"
    ),
    NOTIFICATION(
        "Notifications",
        "Required to send time limit warnings and alerts"
    ),
    ACCESSIBILITY(
        "Accessibility Service",
        "Optional: Provides stronger app blocking (recommended)"
    )
}
