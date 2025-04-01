package com.example.todolist.main.navigation

sealed class NavRoutes(val route: String) {
    data object Home: NavRoutes("Home")
    data object Start: NavRoutes("AppStart")
    data object Collaboration: NavRoutes("Collaboration")
    data object Task: NavRoutes("Task")
    data object About: NavRoutes("About")
}