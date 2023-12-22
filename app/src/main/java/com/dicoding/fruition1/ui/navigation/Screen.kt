package com.dicoding.fruition1.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Detect : Screen("detect")
    object Account : Screen("account")
    object Login : Screen("login")
    object Register : Screen("register")
    object Result : Screen("result")
}