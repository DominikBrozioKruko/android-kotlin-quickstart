package com.example.android_kotlin_quickstart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.android_kotlin_quickstart.data.model.Hotel
import com.example.android_kotlin_quickstart.data.model.Review
import com.example.android_kotlin_quickstart.viewmodel.HotelDetailsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HotelDetailsScreen(viewModel: HotelDetailsViewModel,navController: NavController,onEditHotel: (Hotel) -> Unit) {
    val hotel by viewModel.hotel.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hotel Details") },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack() // <- Go back on press
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(onClick = {
                        hotel?.let { onEditHotel(it) }
                    }) {
                        Text("EDIT",fontSize = 16.sp, color = Color.Blue)
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier
            .padding(paddingValues)
            .padding(horizontal = 16.dp) ) {
            hotel?.let {
                Column(modifier = Modifier
                    .verticalScroll(rememberScrollState())) {

                    Spacer(Modifier.height(16.dp))
                    Text(it.name ?: "", fontSize = 28.sp, fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(4.dp))
                    Text(it.title ?: "")

                    Spacer(Modifier.height(12.dp))
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF2F2F7),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            InfoRow("Address", it.address)
                            InfoRow("City", it.city)
                            InfoRow("State", it.state)
                            InfoRow("Country", it.country)
                            InfoRow("Coordinates", "${it.geo?.lat ?: 0.0}, ${it.geo?.lon ?: 0.0}")
                            InfoRow("Accuracy", it.geo.accuracy)
                            InfoRow("Phone", it.phone)
                            InfoRow("Toll-Free", it.tollfree)
                            InfoRow("Email", it.email)
                            InfoRow("Fax", it.fax)
                            InfoRow("Website", it.url)
                            InfoRow("Check-in Time", it.checkin)
                            InfoRow("Check-out Time", it.checkout)
                            InfoRow("Price", it.price)
                            InfoRow("Vacancy", if (it.vacancy == true) "Available" else "Full")
                            InfoRow("Pets", if (it.petsOk == true) "Allowed" else "No Pets")
                            InfoRow("Free Breakfast", if (it.freeBreakfast == true) "Yes" else "No Free Breakfast")
                            InfoRow("Free Internet", if (it.freeInternet == true) "Yes" else "No Free Internet")
                            InfoRow("Free Parking", if (it.freeParking == true) "Yes" else "No Free Parking")
                        }
                    }

                    Spacer(Modifier.height(12.dp))
                    Text("Reviews", fontWeight = FontWeight.Bold)
                    Spacer(Modifier.height(12.dp))
                    hotel?.reviews?.let { reviews ->
                        if (reviews.isNotEmpty()) {
                            ReviewsSection(reviews)
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }
            } ?: Text("No hotel selected")
        }
    }

}

@Composable
private fun InfoRow(label: String, value: String?) {
    if (!value.isNullOrBlank()) {
        Text("$label: $value")
    }
}

@Composable
fun ReviewsSection(reviews: List<Review>) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = Color(0xFFF2F2F7),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {

            reviews.forEach { review ->
                Column(modifier = Modifier.padding(vertical = 8.dp)) {
                    Text(
                        text = "By: ${review.author}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = review.content,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = "Date: ${review.date}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }
    }
}