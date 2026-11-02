package com.safekids.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.safekids.MainActivity
import com.safekids.R
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationHelper @Inject constructor(
    private val context: Context
) {
    private val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    
    companion object {
        const val CHANNEL_ID_MONITORING = "monitoring_service"
        const val CHANNEL_ID_WARNINGS = "time_limit_warnings"
        const val CHANNEL_ID_ALERTS = "time_limit_alerts"
        
        const val NOTIFICATION_ID_SERVICE = 1001
        const val NOTIFICATION_ID_WARNING = 1002
        const val NOTIFICATION_ID_LIMIT_EXCEEDED = 1003
    }
    
    init {
        createNotificationChannels()
    }
    
    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID_MONITORING,
                "Monitoring Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "SafeKids monitoring service"
                setShowBadge(false)
            }
            
            val warningChannel = NotificationChannel(
                CHANNEL_ID_WARNINGS,
                "Time Limit Warnings",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Warnings when approaching time limits"
            }
            
            val alertChannel = NotificationChannel(
                CHANNEL_ID_ALERTS,
                "Time Limit Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alerts when time limits are exceeded"
            }
            
            notificationManager.createNotificationChannels(listOf(
                serviceChannel, warningChannel, alertChannel
            ))
        }
    }
    
    fun createMonitoringNotification(): android.app.Notification {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        return NotificationCompat.Builder(context, CHANNEL_ID_MONITORING)
            .setContentTitle(context.getString(R.string.monitoring_service_title))
            .setContentText(context.getString(R.string.monitoring_service_description))
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }
    
    fun showTimeLimitWarning(appName: String, remainingMinutes: Int) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_WARNINGS)
            .setContentTitle(context.getString(R.string.time_limit_warning_title))
            .setContentText(context.getString(R.string.time_limit_warning_message, remainingMinutes, appName))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_WARNING, notification)
    }
    
    fun showTimeLimitExceeded(appName: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
        
        val notification = NotificationCompat.Builder(context, CHANNEL_ID_ALERTS)
            .setContentTitle(context.getString(R.string.time_limit_exceeded_title))
            .setContentText(context.getString(R.string.time_limit_exceeded_message, appName))
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .build()
        
        notificationManager.notify(NOTIFICATION_ID_LIMIT_EXCEEDED, notification)
    }
}
