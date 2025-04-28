package com.example.android_kotlin_quickstart.ui

sealed class Screen(val route: String) {
    object HomeScreen : Screen("home_screen")
    object HotelDetailsScreen : Screen("hotel_details_screen")
    object AddEditHotelScreen: Screen("add_edit_hotel_screen")
}