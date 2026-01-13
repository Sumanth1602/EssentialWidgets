package com.example.essentialwidgets.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.ActionParameters
import androidx.glance.action.actionParametersOf
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.layout.width
import androidx.glance.state.GlanceStateDefinition
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextAlign
import androidx.glance.text.TextStyle
import androidx.glance.ColorFilter
import com.example.essentialwidgets.R
import com.example.essentialwidgets.notification.AlarmScheduler
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class NowTimeWidget : GlanceAppWidget() {
    
    override val stateDefinition: GlanceStateDefinition<*> = PreferencesGlanceStateDefinition
    
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            GlanceTheme {
                NowTimeWidgetContent()
            }
        }
    }
    
    companion object {
        val SHOW_RESULT_KEY = booleanPreferencesKey("show_result")
        val CALCULATED_TIME_KEY = longPreferencesKey("calculated_time")
        val CURRENT_TIME_KEY = longPreferencesKey("current_time")
    }
}

@Composable
private fun NowTimeWidgetContent() {
    val prefs = currentState<Preferences>()
    val showResult = prefs[NowTimeWidget.SHOW_RESULT_KEY] ?: false
    val calculatedTimeMillis = prefs[NowTimeWidget.CALCULATED_TIME_KEY] ?: 0L
    val currentTimeMillis = prefs[NowTimeWidget.CURRENT_TIME_KEY] ?: 0L
    
    val formatter = DateTimeFormatter.ofPattern("h:mm a")
    val calculatedTime = if (calculatedTimeMillis > 0) {
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(calculatedTimeMillis),
            ZoneId.systemDefault()
        ).format(formatter)
    } else ""
    
    val currentTime = if (currentTimeMillis > 0) {
        LocalDateTime.ofInstant(
            Instant.ofEpochMilli(currentTimeMillis),
            ZoneId.systemDefault()
        ).format(formatter)
    } else ""
    
    // Calculate time left
    val timeLeftText = if (calculatedTimeMillis > 0) {
        val now = System.currentTimeMillis()
        val remainingMillis = calculatedTimeMillis - now
        if (remainingMillis > 0) {
            val remainingMinutes = remainingMillis / (60 * 1000)
            val hours = remainingMinutes / 60
            val minutes = remainingMinutes % 60
            when {
                hours > 0 && minutes > 0 -> "${hours}h ${minutes}m left"
                hours > 0 -> "${hours}h left"
                minutes > 0 -> "${minutes}m left"
                else -> "Now!"
            }
        } else {
            "Time's up!"
        }
    } else ""
    
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .cornerRadius(28.dp)
            .background(GlanceTheme.colors.widgetBackground)
            .padding(12.dp)
            .clickable(
                actionRunCallback<NowTimeActionCallback>(
                    actionParametersOf(ActionParamKeys.toggleKey to true)
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        if (showResult && calculatedTime.isNotEmpty()) {
            ResultContent(
                calculatedTime = calculatedTime,
                currentTime = currentTime,
                timeLeftText = timeLeftText
            )
        } else {
            IdleContent()
        }
    }
}

@Composable
private fun IdleContent() {
    Row(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left side - description text
        Box(
            modifier = GlanceModifier.defaultWeight(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Adds 3h 30m to\ncurrent time",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurface,
                    fontSize = 13.sp,
                    textAlign = TextAlign.Center
                )
            )
        }
        
        Spacer(modifier = GlanceModifier.width(8.dp))
        
        // Right side - NOW button
        Box(
            modifier = GlanceModifier
                .cornerRadius(24.dp)
                .background(GlanceTheme.colors.primary)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    provider = ImageProvider(R.drawable.ic_touch_app),
                    contentDescription = "Tap",
                    modifier = GlanceModifier.size(18.dp),
                    colorFilter = ColorFilter.tint(GlanceTheme.colors.onPrimary)
                )
                Spacer(modifier = GlanceModifier.width(6.dp))
                Text(
                    text = "NOW",
                    style = TextStyle(
                        color = GlanceTheme.colors.onPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun ResultContent(
    calculatedTime: String,
    currentTime: String,
    timeLeftText: String
) {
    Row(
        modifier = GlanceModifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Left - "Ready at"
        Text(
            text = "Ready at",
            style = TextStyle(
                color = GlanceTheme.colors.onSurface,
                fontSize = 13.sp
            )
        )
        
        Spacer(modifier = GlanceModifier.width(12.dp))
        
        // Center - Time pill
        Box(
            modifier = GlanceModifier
                .cornerRadius(20.dp)
                .background(GlanceTheme.colors.surfaceVariant)
                .padding(horizontal = 16.dp, vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = calculatedTime,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }
        
        Spacer(modifier = GlanceModifier.width(12.dp))
        
        // Right - "From X:XX" and "Xh left"
        Column(
            horizontalAlignment = Alignment.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "From $currentTime",
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
            Text(
                text = timeLeftText,
                style = TextStyle(
                    color = GlanceTheme.colors.onSurfaceVariant,
                    fontSize = 11.sp
                )
            )
        }
    }
}

object ActionParamKeys {
    val toggleKey = ActionParameters.Key<Boolean>("toggle")
}

class NowTimeActionCallback : ActionCallback {
    override suspend fun onAction(
        context: Context,
        glanceId: GlanceId,
        parameters: ActionParameters
    ) {
        var targetTimeMillis = 0L
        var shouldScheduleNotifications = false
        
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentShowResult = prefs[NowTimeWidget.SHOW_RESULT_KEY] ?: false
            
            if (currentShowResult) {
                // Reset to idle state
                prefs[NowTimeWidget.SHOW_RESULT_KEY] = false
                prefs[NowTimeWidget.CALCULATED_TIME_KEY] = 0L
                prefs[NowTimeWidget.CURRENT_TIME_KEY] = 0L
                
                // Cancel scheduled notifications
                AlarmScheduler.cancelNotifications(context)
            } else {
                // Calculate and show result
                val now = System.currentTimeMillis()
                val threeAndHalfHoursInMillis = (3 * 60 + 30) * 60 * 1000L
                val futureTime = now + threeAndHalfHoursInMillis
                
                prefs[NowTimeWidget.SHOW_RESULT_KEY] = true
                prefs[NowTimeWidget.CALCULATED_TIME_KEY] = futureTime
                prefs[NowTimeWidget.CURRENT_TIME_KEY] = now
                
                // Schedule notifications
                targetTimeMillis = futureTime
                shouldScheduleNotifications = true
            }
        }
        
        // Schedule notifications outside of the state update
        if (shouldScheduleNotifications && targetTimeMillis > 0) {
            AlarmScheduler.scheduleNotifications(context, targetTimeMillis)
        }
        
        // Update the widget
        NowTimeWidget().update(context, glanceId)
    }
}
