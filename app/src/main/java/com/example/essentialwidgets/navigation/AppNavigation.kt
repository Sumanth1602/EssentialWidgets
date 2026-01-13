package com.example.essentialwidgets.navigation

import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.essentialwidgets.data.PreferencesManager
import com.example.essentialwidgets.ui.home.HomeScreen
import com.example.essentialwidgets.ui.onboarding.OnboardingScreen

sealed class Screen(val route: String) {
    data object Onboarding : Screen("onboarding")
    data object Home : Screen("home")
}

@Composable
fun AppNavigation(
    preferencesManager: PreferencesManager,
    navController: NavHostController = rememberNavController()
) {
    val hasCompletedOnboarding by preferencesManager.hasCompletedOnboarding.collectAsState(initial = null)
    
    // Wait for preference to load
    if (hasCompletedOnboarding == null) return
    
    val startDestination = if (hasCompletedOnboarding == true) {
        Screen.Home.route
    } else {
        Screen.Onboarding.route
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        composable(
            route = Screen.Onboarding.route,
            enterTransition = {
                fadeIn(animationSpec = tween(300))
            },
            exitTransition = {
                slideOutOfContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeOut(animationSpec = tween(300))
            }
        ) {
            OnboardingScreen(
                onCompleteOnboarding = {
                    navController.navigate(Screen.Home.route) {
                        popUpTo(Screen.Onboarding.route) { inclusive = true }
                    }
                },
                preferencesManager = preferencesManager
            )
        }
        
        composable(
            route = Screen.Home.route,
            enterTransition = {
                slideIntoContainer(
                    towards = AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                ) + fadeIn(animationSpec = tween(300))
            }
        ) {
            HomeScreen()
        }
    }
}

