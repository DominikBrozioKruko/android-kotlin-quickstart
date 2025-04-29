package com.example.android_kotlin_quickstart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.material.DismissDirection
import androidx.compose.material.DismissValue
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.SwipeToDismiss
import androidx.compose.material.rememberDismissState
import androidx.compose.material3.Icon
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.LocationOn

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onHotelSelected: (Hotel) -> Unit,
    onAddHotel:() -> Unit,
    onEditHotel: (Hotel) -> Unit) {
    val uiState = remember { viewModel.uiState }
    var queryState = viewModel.liveQueryState.observeAsState(initial = null)
    val errorMessage by ErrorManager.errorState.collectAsState()


    Box(
        modifier = Modifier
            .fillMaxSize(),
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
                        SwipeableHotelCard(hotel,onHotelSelected = onHotelSelected, onEditHotel = onEditHotel)
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
                .background(MaterialTheme.colorScheme.surface)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .height(56.dp)
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.TopCenter),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Hotel management app",
                color = MaterialTheme.colorScheme.background,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.Center)
            )

            IconButton(
                onClick = {
                    onAddHotel()
                },
                modifier = Modifier
                    .align(Alignment.CenterEnd)
                    .padding(end = 5.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add",
                    tint = MaterialTheme.colorScheme.background
                )
            }
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableHotelCard(hotel: Hotel, onHotelSelected: (Hotel) -> Unit, onEditHotel: (Hotel) -> Unit) {
    val dismissState = rememberDismissState()

    LaunchedEffect(dismissState.currentValue) {
        when {
            dismissState.isDismissed(DismissDirection.StartToEnd) -> {
                onEditHotel(hotel)
                dismissState.reset()
            }
            dismissState.isDismissed(DismissDirection.EndToStart) -> {
                //onDelete(item)
                dismissState.reset()
            }
        }
    }

    SwipeToDismiss(
        state = dismissState,
        directions = setOf(
            DismissDirection.StartToEnd,  // Edit
            DismissDirection.EndToStart   // Delete
        ),
        background = {
            val direction = dismissState.dismissDirection ?: return@SwipeToDismiss
            val color = when (direction) {
                DismissDirection.StartToEnd -> MaterialTheme.colorScheme.primary
                DismissDirection.EndToStart -> MaterialTheme.colorScheme.surface
            }

            val icon = when (direction) {
                DismissDirection.StartToEnd -> Icons.Default.Edit
                DismissDirection.EndToStart -> Icons.Default.Delete
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color)
                    .padding(horizontal = 20.dp),
                contentAlignment = when (direction) {
                    DismissDirection.StartToEnd -> Alignment.CenterStart
                    DismissDirection.EndToStart -> Alignment.CenterEnd
                }
            ) {
                Icon(icon, contentDescription = null, tint = Color.White)
            }
        },
        dismissContent = {
            HotelCard(hotel = hotel, onHotelSelected = onHotelSelected)
        }
    )
}

@Composable
fun HotelCard(hotel: Hotel,onHotelSelected: (Hotel) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background),
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    indication = null,
                    interactionSource = remember { MutableInteractionSource() }
                ) {
                    onHotelSelected(hotel)
                }
                .padding(5.dp)
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
                    color = MaterialTheme.colorScheme.secondary
                ),
                modifier = Modifier.padding(bottom = 5.dp)
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.LocationOn,
                    contentDescription = "Location Icon",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "${hotel.address ?: ""} ${hotel.city ?: ""} ${hotel.country}",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    ),
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Filled.Call,
                    contentDescription = "Location Icon",
                    tint = MaterialTheme.colorScheme.tertiary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = hotel.phone ?: "Not known",
                    style = TextStyle(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.tertiary
                    )
                )
            }
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
                detectTapGestures { }
            }
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.background,
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
                    color = MaterialTheme.colorScheme.secondary
                )

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = error.message,
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.tertiary,
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
