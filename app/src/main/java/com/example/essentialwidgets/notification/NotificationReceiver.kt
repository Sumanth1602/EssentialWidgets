package com.example.essentialwidgets.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_REMINDER = "com.example.essentialwidgets.ACTION_REMINDER"
        const val ACTION_TARGET_TIME = "com.example.essentialwidgets.ACTION_TARGET_TIME"
        const val EXTRA_TARGET_TIME = "extra_target_time"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val targetTime = intent.getStringExtra(EXTRA_TARGET_TIME) ?: return
        
        when (intent.action) {
            ACTION_REMINDER -> {
                NotificationHelper.showReminderNotification(context, targetTime)
            }
            ACTION_TARGET_TIME -> {
                NotificationHelper.showTargetTimeNotification(context, targetTime)
            }
        }
    }
}

