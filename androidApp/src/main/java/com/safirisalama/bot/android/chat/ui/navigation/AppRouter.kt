package com.safirisalama.bot.android.chat.ui.navigation

class AppRouter {
    private object Route {
        const val SPLASH = "splash"
        const val GENERATE_ITINERARY = "generate_itinerary"
        const val HOME = "home/{message}"
    }

    sealed class Screen(val route: String) {
        object Splash : Screen(Route.SPLASH)
        object Home : Screen(Route.HOME)
        object GenerateItinerary : Screen(Route.GENERATE_ITINERARY)
    }
}
