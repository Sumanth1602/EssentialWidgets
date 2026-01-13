package com.example.essentialwidgets

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.example.essentialwidgets.data.PreferencesManager
import com.example.essentialwidgets.navigation.AppNavigation
import com.example.essentialwidgets.notification.NotificationHelper
import com.example.essentialwidgets.ui.theme.EssentialWidgetsTheme

class MainActivity : ComponentActivity() {
    
    private lateinit var preferencesManager: PreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        preferencesManager = PreferencesManager(applicationContext)
        
        // Create notification channel
        NotificationHelper.createNotificationChannel(this)
        
        enableEdgeToEdge()
        setContent {
            EssentialWidgetsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavigation(preferencesManager = preferencesManager)
                }
            }
        }
    }
}
