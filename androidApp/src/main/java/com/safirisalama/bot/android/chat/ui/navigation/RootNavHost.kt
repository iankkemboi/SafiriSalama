package com.safirisalama.bot.android.chat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.safirisalama.bot.android.chat.ui.ChatScreen
import com.safirisalama.bot.android.chat.ui.SplashScreen

@Composable
fun RootNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = AppRouter.Screen.Splash.route,
    ) {
        composable(AppRouter.Screen.Splash.route) {
            SplashScreen(navController = navController)
        }
        composable(AppRouter.Screen.Home.route) {
            ChatScreen()
        }
    }
}
