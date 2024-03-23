package com.safirisalama.bot.android.chat.ui.navigation

class AppRouter {
    private object Route {
        const val SPLASH = "splash"
        const val HOME = "home"
    }

    sealed class Screen(val route: String) {
        object Splash : Screen(Route.SPLASH)
        object Home : Screen(Route.HOME)
    }
}
