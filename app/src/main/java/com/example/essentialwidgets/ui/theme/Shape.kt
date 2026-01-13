package com.example.essentialwidgets.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Material 3 Expressive Shapes
// Larger, more organic corner radii for a softer, expressive feel

val Shapes = Shapes(
    // Extra small - Chips, small buttons
    extraSmall = RoundedCornerShape(8.dp),
    
    // Small - Text fields, list items
    small = RoundedCornerShape(12.dp),
    
    // Medium - Cards, dialogs
    medium = RoundedCornerShape(20.dp),
    
    // Large - Bottom sheets, navigation drawers
    large = RoundedCornerShape(28.dp),
    
    // Extra large - Full screen dialogs
    extraLarge = RoundedCornerShape(32.dp)
)

// Custom expressive shapes for specific use cases
val WidgetCardShape = RoundedCornerShape(28.dp)
val ExpressiveButtonShape = RoundedCornerShape(24.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp)

