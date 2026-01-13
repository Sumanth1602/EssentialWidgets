package com.example.essentialwidgets.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.essentialwidgets.data.WidgetPreferences
import com.example.essentialwidgets.ui.theme.BottomSheetShape
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class WidgetInfo(
    val id: String,
    val name: String,
    val description: String,
    val widthCells: Int,
    val heightCells: Int,
    val previewContent: @Composable () -> Unit
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    var selectedWidget by remember { mutableStateOf<WidgetInfo?>(null) }
    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    
    var isVisible by remember { mutableStateOf(false) }
    
    // Get current duration for display
    val durationText = remember { WidgetPreferences.getDurationText(context) }
    
    val availableWidgets = remember {
        listOf(
            WidgetInfo(
                id = "now_time",
                name = "Now + Time",
                description = "Add custom time to current time with one tap",
                widthCells = 2,
                heightCells = 1,
                previewContent = { NowTimeWidgetPreview() }
            )
        )
    }
    
    LaunchedEffect(Unit) {
        delay(100)
        isVisible = true
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.surface,
                        MaterialTheme.colorScheme.surfaceContainerLow
                    )
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
        ) {
            // Header
            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn() + slideInVertically(
                    initialOffsetY = { -it },
                    animationSpec = spring(stiffness = Spring.StiffnessLow)
                )
            ) {
                Column(
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 16.dp)
                ) {
                    Text(
                        text = "Essential Widgets",
                        style = MaterialTheme.typography.headlineLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Tap a widget to configure and add to home screen",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Widget Grid
            LazyVerticalStaggeredGrid(
                columns = StaggeredGridCells.Fixed(1),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalItemSpacing = 12.dp
            ) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it / 2 },
                            animationSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioLowBouncy
                            )
                        )
                    ) {
                        Text(
                            text = "Time Widgets",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
                        )
                    }
                }
                
                items(
                    items = availableWidgets,
                    key = { it.id }
                ) { widget ->
                    AnimatedVisibility(
                        visible = isVisible,
                        enter = fadeIn() + slideInVertically(
                            initialOffsetY = { it },
                            animationSpec = spring(
                                stiffness = Spring.StiffnessLow,
                                dampingRatio = Spring.DampingRatioMediumBouncy
                            )
                        )
                    ) {
                        WidgetGridItem(
                            widget = widget,
                            onClick = {
                                selectedWidget = widget
                            }
                        )
                    }
                }
            }
        }
        
        // Bottom Sheet
        if (selectedWidget != null) {
            ModalBottomSheet(
                onDismissRequest = {
                    selectedWidget = null
                },
                sheetState = sheetState,
                shape = BottomSheetShape,
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow
            ) {
                AddToHomeSheet(
                    widget = selectedWidget!!,
                    onDismiss = {
                        scope.launch {
                            sheetState.hide()
                            selectedWidget = null
                        }
                    },
                    context = context
                )
            }
        }
    }
}
