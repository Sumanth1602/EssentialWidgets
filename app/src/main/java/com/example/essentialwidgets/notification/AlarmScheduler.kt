package com.example.essentialwidgets.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

object AlarmScheduler {
    
    private const val REQUEST_CODE_REMINDER = 2001
    private const val REQUEST_CODE_TARGET = 2002
    
    fun scheduleNotifications(context: Context, targetTimeMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        
        // Check if we can schedule exact alarms
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (!alarmManager.canScheduleExactAlarms()) {
                // Fall back to inexact alarms or prompt user
                scheduleInexactAlarms(context, targetTimeMillis)
                return
            }
        }
        
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val targetTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(targetTimeMillis),
            ZoneId.systemDefault()
        ).format(formatter)
        
        // Schedule 10-minute reminder
        val reminderTimeMillis = targetTimeMillis - (10 * 60 * 1000) // 10 minutes before
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
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                reminderPendingIntent
            )
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
            
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTimeMillis,
                targetPendingIntent
            )
        }
    }
    
    private fun scheduleInexactAlarms(context: Context, targetTimeMillis: Long) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        
        val formatter = DateTimeFormatter.ofPattern("h:mm a")
        val targetTime = LocalDateTime.ofInstant(
            Instant.ofEpochMilli(targetTimeMillis),
            ZoneId.systemDefault()
        ).format(formatter)
        
        // Schedule 10-minute reminder with inexact alarm
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
            
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                reminderTimeMillis,
                reminderPendingIntent
            )
        }
        
        // Schedule target time notification with inexact alarm
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
            
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                targetTimeMillis,
                targetPendingIntent
            )
        }
    }
    
    fun cancelNotifications(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        
        val reminderIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_REMINDER
        }
        val reminderPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_REMINDER,
            reminderIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(reminderPendingIntent)
        
        val targetIntent = Intent(context, NotificationReceiver::class.java).apply {
            action = NotificationReceiver.ACTION_TARGET_TIME
        }
        val targetPendingIntent = PendingIntent.getBroadcast(
            context,
            REQUEST_CODE_TARGET,
            targetIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(targetPendingIntent)
        
        // Also cancel any shown notifications
        NotificationHelper.cancelAllNotifications(context)
    }
}

