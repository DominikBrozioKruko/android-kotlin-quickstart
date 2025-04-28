package com.example.android_kotlin_quickstart.ui

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.android_kotlin_quickstart.ui.screens.AddEditHotelScreen
import com.example.android_kotlin_quickstart.ui.screens.HomeScreen
import com.example.android_kotlin_quickstart.ui.screens.HotelDetailsScreen
import com.example.android_kotlin_quickstart.viewmodel.AddEditHotelViewModel
import com.example.android_kotlin_quickstart.viewmodel.HomeViewModel
import com.example.android_kotlin_quickstart.viewmodel.HotelDetailsViewModel
import com.example.android_kotlin_quickstart.viewmodel.ViewMode

@Composable
fun AppNavGraph(
    homeViewModel: HomeViewModel,
    hotelDetailsViewModel: HotelDetailsViewModel = viewModel(),
    addEditHotelViewModel: AddEditHotelViewModel = viewModel()
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.HomeScreen.route) {

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                viewModel = homeViewModel,
                onHotelSelected = { selectedHotel ->
                    hotelDetailsViewModel.setHotel(selectedHotel)
                    navController.navigate(Screen.HotelDetailsScreen.route)
                },
                onAddHotel = {
                    addEditHotelViewModel.setViewMode(ViewMode.Add)
                    navController.navigate(Screen.AddEditHotelScreen.route)
                } ,
                onEditHotel = { hotel ->
                    addEditHotelViewModel.setViewMode(ViewMode.Edit(hotel))
                    navController.navigate(Screen.AddEditHotelScreen.route)
                },
            )
        }

        composable(Screen.HotelDetailsScreen.route) {
            HotelDetailsScreen(viewModel = hotelDetailsViewModel,
                navController = navController,
                onEditHotel = { hotel ->
                    addEditHotelViewModel.setViewMode(ViewMode.Edit(hotel))
                    navController.navigate(Screen.AddEditHotelScreen.route)
                }
                )
        }

        composable(Screen.AddEditHotelScreen.route) {
            AddEditHotelScreen(viewModel = addEditHotelViewModel,
                navController = navController)
        }
    }
}