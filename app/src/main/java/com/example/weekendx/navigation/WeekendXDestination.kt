package com.example.weekendx.navigation

sealed class WeekendXDestination(val route: String) {
    object Splash : WeekendXDestination("splash")
    object Home : WeekendXDestination("home")
    object History : WeekendXDestination("history")
    object Settings : WeekendXDestination("settings")
    object About : WeekendXDestination("about")
}
