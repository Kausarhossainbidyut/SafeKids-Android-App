package com.safekids.core.service

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import com.safekids.domain.repository.TimeLimitRepository
import com.safekids.domain.repository.UsageStatsRepository
import com.safekids.util.NotificationHelper
import com.safekids.util.UsageStatsManager
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.*
import javax.inject.Inject

@AndroidEntryPoint
class UsageMonitorService : Service() {
    
    @Inject
    lateinit var usageStatsManager: UsageStatsManager
    
    @Inject
    lateinit var usageStatsRepository: UsageStatsRepository
    
    @Inject
    lateinit var timeLimitRepository: TimeLimitRepository
    
    @Inject
    lateinit var notificationHelper: NotificationHelper
    
    private val serviceScope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private val monitoringInterval = 30_000L // 30 seconds
    
    private val monitoringRunnable = object : Runnable {
        override fun run() {
            serviceScope.launch {
                checkUsageAndLimits()
            }
            handler.postDelayed(this, monitoringInterval)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        startForeground(
            NotificationHelper.NOTIFICATION_ID_SERVICE,
            notificationHelper.createMonitoringNotification()
        )
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startMonitoring()
        return START_STICKY
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        stopMonitoring()
        serviceScope.cancel()
    }
    
    private fun startMonitoring() {
        handler.post(monitoringRunnable)
    }
    
    private fun stopMonitoring() {
        handler.removeCallbacks(monitoringRunnable)
    }
    
    private suspend fun checkUsageAndLimits() {
        try {
            // Sync usage stats from system
            usageStatsRepository.syncUsageStats()
            
            // Get all active time limits
            val limits = timeLimitRepository.getAllActiveLimits()
            
            limits.forEach { limit ->
                // Get current usage for this app
                val currentUsage = usageStatsManager.getTodayUsageForApp(limit.packageName)
                
                // Update usage in database
                timeLimitRepository.updateCurrentUsage(limit.packageName, currentUsage)
                
                // Check if limit exceeded
                val limitMillis = limit.getCurrentLimit()
                val isExceeded = currentUsage >= limitMillis
                
                if (isExceeded && !limit.limitExceeded) {
                    // Limit just exceeded
                    timeLimitRepository.updateLimitExceeded(limit.packageName, true)
                    notificationHelper.showTimeLimitExceeded(limit.appName)
                    
                    // Add block log
                    timeLimitRepository.addBlockLog(
                        packageName = limit.packageName,
                        appName = limit.appName,
                        reason = "TIME_LIMIT_EXCEEDED"
                    )
                    
                    // TODO: Launch lock screen
                    launchLockScreen(limit.packageName, limit.appName)
                } else if (!isExceeded && limit.shouldShowWarning()) {
                    // Show warning
                    val remainingMillis = limitMillis - currentUsage
                    val remainingMinutes = (remainingMillis / (1000 * 60)).toInt()
                    
                    notificationHelper.showTimeLimitWarning(limit.appName, remainingMinutes)
                    timeLimitRepository.markWarningShown(limit.packageName)
                }
            }
        } catch (e: Exception) {
            // Handle errors silently
        }
    }
    
    private fun launchLockScreen(packageName: String, appName: String) {
        // TODO: Launch LockScreenActivity
        // This will be implemented after creating LockScreenActivity
    }
}
