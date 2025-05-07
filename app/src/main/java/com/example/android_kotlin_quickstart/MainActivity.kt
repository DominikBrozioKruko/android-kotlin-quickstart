package com.example.android_kotlin_quickstart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.android_kotlin_quickstart.ui.AppNavGraph
import com.example.android_kotlin_quickstart.ui.theme.Android_kotlin_quickstartTheme
import com.example.android_kotlin_quickstart.ui.screens.HomeScreen
import com.example.android_kotlin_quickstart.viewmodel.AddEditHotelViewModel
import com.example.android_kotlin_quickstart.viewmodel.HomeViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val homeViewModel by viewModel<HomeViewModel>()
    private val addEditHotelViewModel by viewModel<AddEditHotelViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Android_kotlin_quickstartTheme {
                AppNavGraph(homeViewModel = homeViewModel,addEditHotelViewModel = addEditHotelViewModel)
            }
        }
    }
}
