package com.example.essentialwidgets.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlarmScheduler {
    
    private const val REQUEST_CODE_REMINDER = 2001
    private const val REQUEST_CODE_TARGET = 2002
    private const val REQUEST_CODE_WIDGET_REFRESH = 2003
    private const val REFRESH_INTERVAL_MILLIS = 60_000L
    
    fun scheduleNotifications(context: Context, targetTimeMillis: Long) {
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val targetTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(targetTimeMillis),
            ZoneId.systemDefault()
        ).format(formatter)
        
        // Schedule 10-minute reminder
        val reminderTimeMillis = targetTimeMillis - (10 * 60 * 1000)
        if (reminderTimeMillis > System.currentTimeMillis()) {
            val reminderIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_REMINDER
                putExtra(NotificationReceiver.EXTRA_TARGET_TIME, targetTime)
            }
            val reminderPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_REMINDER,
                reminderIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            scheduleAlarm(context, reminderTimeMillis, reminderPendingIntent)
        }
        
        // Schedule target time notification
        if (targetTimeMillis > System.currentTimeMillis()) {
            val targetIntent = Intent(context, NotificationReceiver::class.java).apply {
                action = NotificationReceiver.ACTION_TARGET_TIME
                putExtra(NotificationReceiver.EXTRA_TARGET_TIME, targetTime)
            }
            val targetPendingIntent = PendingIntent.getBroadcast(
                context,
                REQUEST_CODE_TARGET,
                targetIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            scheduleAlarm(context, targetTimeMillis, targetPendingIntent)
        }
    }
    
    fun scheduleWidgetRefresh(context: Context, targetTimeMillis: Long) {
        val nextRefreshTimeMillis = System.currentTimeMillis() + REFRESH_INTERVAL_MILLIS
        if (nextRefreshTimeMillis >= targetTimeMillis) {
            cancelWidgetRefresh(context)
            return
        }
        
        val refreshIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_WIDGET_REFRESH
            putExtra(NotificationReceiver.EXTRA_TARGET_TIME_MILLIS, targetTimeMillis)
        }
        val refreshPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_WIDGET_REFRESH,
            refreshIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        scheduleAlarm(context, nextRefreshTimeMillis, refreshPendingIntent)
    }
    
    fun cancelNotifications(context: Context) {
        cancelAlarm(context, REQUEST_CODE_REMINDER, NotificationReceiver.ACTION_REMINDER)
        cancelAlarm(context, REQUEST_CODE_TARGET, NotificationReceiver.ACTION_TARGET_TIME)
        
        // Also cancel any shown notifications
        NotificationHelper.cancelAllNotifications(context)
    }
    
    fun cancelWidgetRefresh(context: Context) {
        cancelAlarm(context, REQUEST_CODE_WIDGET_REFRESH, NotificationReceiver.ACTION_WIDGET_REFRESH)
    }
    
    private fun scheduleAlarm(
        context: Context,
        triggerAtMillis: Long,
        pendingIntent: PendingIntent
    ) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        if (canScheduleExactAlarms(alarmManager)) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        } else {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }
    }
    
    private fun cancelAlarm(context: Context, requestCode: Int, action: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, NotificationReceiver::class.java).apply {
            this.action = action
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
        pendingIntent.cancel()
    }
    
    private fun canScheduleExactAlarms(alarmManager: AlarmManager): Boolean {
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.S || alarmManager.canScheduleExactAlarms()
    }
}
