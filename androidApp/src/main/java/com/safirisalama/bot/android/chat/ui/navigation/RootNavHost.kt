package com.safirisalama.bot.android.chat.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.safirisalama.bot.android.chat.ui.ChatScreen
import com.safirisalama.bot.android.chat.ui.GenerateItineraryScreen
import com.safirisalama.bot.android.chat.ui.SimpleChatScreen
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
        composable(AppRouter.Screen.GenerateItinerary.route) {
            GenerateItineraryScreen(navController = navController)
        }
        composable(
            route = AppRouter.Screen.Home.route,
            arguments = listOf(navArgument("message") {
                type = NavType.StringType
            })
        ) { backStackEntry ->
            val message = backStackEntry.arguments?.getString("message")
            if (message != null) {
                SimpleChatScreen( message = message)
            }
        }
    }
}
