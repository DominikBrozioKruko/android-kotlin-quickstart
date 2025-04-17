package com.example.android_kotlin_quickstart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.android_kotlin_quickstart.ErrorManager
import com.example.android_kotlin_quickstart.data.model.AppError
import com.example.android_kotlin_quickstart.data.model.Hotel
import com.example.android_kotlin_quickstart.ui.theme.CouchbaseRed
import com.example.android_kotlin_quickstart.viewmodel.HomeViewModel

@Composable
fun HomeScreen(viewModel: HomeViewModel, onHotelSelected: (Hotel) -> Unit) {
    val uiState = remember { viewModel.uiState }
    var queryState = viewModel.liveQueryState.observeAsState(initial = null)
    val errorMessage by ErrorManager.errorState.collectAsState()


    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 56.dp)
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            queryState.value?.let { queryStateValue ->
                LazyColumn {
                    items(queryStateValue) { hotel ->
                        HotelCard(hotel,onHotelSelected = onHotelSelected)
                    }
                }
            } ?: run {
                Text(
                    text = uiState,
                    style = MaterialTheme.typography.headlineMedium
                )
            }
        }


        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(CouchbaseRed)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .height(56.dp)
                .background(CouchbaseRed)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hotel management app",
                color = Color.White,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Box(modifier = Modifier.fillMaxSize()) {
            errorMessage?.let { errorModel ->
                ErrorBanner(
                    error = errorModel,
                    onDismiss = { ErrorManager.clearError() }
                )
            }
        }
    }
}

@Composable
fun HotelCard(hotel: Hotel,onHotelSelected: (Hotel) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onHotelSelected(hotel)
            }
            .padding(10.dp)
            .graphicsLayer {
                shadowElevation = 5f
                shape = RoundedCornerShape(5.dp)
                clip = true
                translationY = 3f
            }
            .background(Color.White, RoundedCornerShape(5.dp))
            .padding(16.dp),

        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = hotel.name ?: "",
            style = TextStyle(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp,
                color = Color.Black
            ),
            modifier = Modifier.padding(bottom = 5.dp)
        )

        Text(
            text = "${hotel.address ?: ""} ${hotel.city ?: ""} ${hotel.country}",
            style = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontSize = 16.sp,
                color = Color.Gray
            ),
            modifier = Modifier.padding(bottom = 5.dp)
        )

        hotel.phone?.let {
            Text(
                text = it,
                style = TextStyle(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp,
                    color = Color.Gray
                )
            )
        }
    }
}

@Composable
fun ErrorBanner(
    error: AppError,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.3f))
            .pointerInput(Unit) {
                detectTapGestures {  }
            }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            tonalElevation = 8.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = error.title,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = error.message,
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    textAlign = TextAlign.Center
                )

                if (error.showDismissButton) {
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(onClick = onDismiss) {
                        Text("OK")
                    }
                }
            }
        }
    }
}
