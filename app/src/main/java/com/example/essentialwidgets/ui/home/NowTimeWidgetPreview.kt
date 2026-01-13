package com.example.essentialwidgets.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.essentialwidgets.R
import com.example.essentialwidgets.data.WidgetPreferences
import kotlinx.coroutines.delay
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Composable
fun NowTimeWidgetPreview() {
    val context = LocalContext.current
    var showResult by remember { mutableStateOf(false) }
    val currentTime = remember { LocalTime.now() }
    
    // Get configured duration
    val hours = remember { WidgetPreferences.getHours(context) }
    val minutes = remember { WidgetPreferences.getMinutes(context) }
    val futureTime = remember { currentTime.plusHours(hours.toLong()).plusMinutes(minutes.toLong()) }
    val formatter = remember { DateTimeFormatter.ofPattern("h:mm a") }
    
    // Format duration text
    val durationText = remember {
        when {
            hours > 0 && minutes > 0 -> "Adds ${hours}h ${minutes}m to\ncurrent time"
            hours > 0 -> "Adds ${hours}h to\ncurrent time"
            minutes > 0 -> "Adds ${minutes}m to\ncurrent time"
            else -> "Tap NOW"
        }
    }
    
    val timeLeftText = remember {
        when {
            hours > 0 && minutes > 0 -> "${hours}h ${minutes}m left"
            hours > 0 -> "${hours}h left"
            minutes > 0 -> "${minutes}m left"
            else -> ""
        }
    }
    
    // Animate between states for preview effect
    LaunchedEffect(Unit) {
        while (true) {
            delay(3000)
            showResult = !showResult
        }
    }
    
    Row(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        if (showResult) {
            // Result state: Ready at | Time pill | From X:XX / Xh left
            
            // Left - "Ready at"
            Text(
                text = "Ready at",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            // Center - Time pill
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant)
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                Text(
                    text = futureTime.format(formatter),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Right - From time & time left
            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "From ${currentTime.format(formatter)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
                Text(
                    text = timeLeftText,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 9.sp
                )
            }
        } else {
            // Idle state: Description | NOW button
            
            // Left side - description
            Text(
                text = durationText,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Start,
                lineHeight = 14.sp
            )
            
            // Right side - NOW button
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(18.dp))
                    .background(MaterialTheme.colorScheme.primary)
                    .padding(horizontal = 14.dp, vertical = 8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_touch_app),
                        contentDescription = null,
                        modifier = Modifier.size(14.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onPrimary)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "NOW",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
        }
    }
}
