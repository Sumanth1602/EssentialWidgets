package com.example.essentialwidgets.data

import android.content.Context
import android.content.SharedPreferences

object WidgetPreferences {
    
    private const val PREFS_NAME = "widget_preferences"
    private const val KEY_HOURS = "duration_hours"
    private const val KEY_MINUTES = "duration_minutes"
    
    // Default: 3 hours 30 minutes
    private const val DEFAULT_HOURS = 3
    private const val DEFAULT_MINUTES = 30
    
    private fun getPrefs(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveDuration(context: Context, hours: Int, minutes: Int) {
        getPrefs(context).edit()
            .putInt(KEY_HOURS, hours)
            .putInt(KEY_MINUTES, minutes)
            .apply()
    }
    
    fun getHours(context: Context): Int {
        return getPrefs(context).getInt(KEY_HOURS, DEFAULT_HOURS)
    }
    
    fun getMinutes(context: Context): Int {
        return getPrefs(context).getInt(KEY_MINUTES, DEFAULT_MINUTES)
    }
    
    fun getDurationMillis(context: Context): Long {
        val hours = getHours(context)
        val minutes = getMinutes(context)
        return ((hours * 60L) + minutes) * 60L * 1000L
    }
    
    fun getDurationText(context: Context): String {
        val hours = getHours(context)
        val minutes = getMinutes(context)
        return when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m"
            hours > 0 -> "${hours}h"
            minutes > 0 -> "${minutes}m"
            else -> "0m"
        }
    }
}
