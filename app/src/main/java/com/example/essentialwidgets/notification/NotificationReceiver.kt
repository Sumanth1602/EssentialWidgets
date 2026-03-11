package com.example.essentialwidgets.notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.updateAll
import com.example.essentialwidgets.widget.NowTimeWidget
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotificationReceiver : BroadcastReceiver() {
    
    companion object {
        const val ACTION_REMINDER = "com.example.essentialwidgets.ACTION_REMINDER"
        const val ACTION_TARGET_TIME = "com.example.essentialwidgets.ACTION_TARGET_TIME"
        const val ACTION_WIDGET_REFRESH = "com.example.essentialwidgets.ACTION_WIDGET_REFRESH"
        const val EXTRA_TARGET_TIME = "extra_target_time"
        const val EXTRA_TARGET_TIME_MILLIS = "extra_target_time_millis"
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        val appContext = context.applicationContext
        
        CoroutineScope(Dispatchers.Default).launch {
            try {
                when (intent.action) {
                    ACTION_REMINDER -> {
                        val targetTime = intent.getStringExtra(EXTRA_TARGET_TIME) ?: return@launch
                        NotificationHelper.showReminderNotification(appContext, targetTime)
                    }
                    ACTION_TARGET_TIME -> {
                        val targetTime = intent.getStringExtra(EXTRA_TARGET_TIME) ?: return@launch
                        NotificationHelper.showTargetTimeNotification(appContext, targetTime)
                        AlarmScheduler.cancelWidgetRefresh(appContext)
                        NowTimeWidget().updateAll(appContext)
                    }
                    ACTION_WIDGET_REFRESH -> {
                        val targetTimeMillis = intent.getLongExtra(EXTRA_TARGET_TIME_MILLIS, 0L)
                        NowTimeWidget().updateAll(appContext)
                        if (targetTimeMillis > System.currentTimeMillis()) {
                            AlarmScheduler.scheduleWidgetRefresh(appContext, targetTimeMillis)
                        } else {
                            AlarmScheduler.cancelWidgetRefresh(appContext)
                        }
                    }
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
