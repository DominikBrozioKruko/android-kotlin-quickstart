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
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onHotelSelected: (Hotel) -> Unit,
    onAddHotel:() -> Unit,
    onEditHotel: (Hotel) -> Unit) {
    
    // Collect StateFlow values
    val uiState by viewModel.uiState.collectAsState()
    val hotelList by viewModel.hotelList.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorState by viewModel.errorState.collectAsState()
    val searchText by viewModel.searchText.collectAsState()
    val errorMessage by ErrorManager.errorState.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 120.dp) // Increased padding to account for taller header
                .padding(WindowInsets.statusBars.asPaddingValues())
        ) {
            // Sort by name row
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("SortByNameRow"),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sort by name",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    modifier = Modifier
                        .clickable { viewModel.onSortButtonTapped() }
                        .padding(end = 4.dp)
                )
                Icon(
                    imageVector = if (viewModel.descendingList) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = "Sort",
                    tint = Color.Gray,
                    modifier = Modifier.clickable { viewModel.onSortButtonTapped() }
                )
            }
            
            // Content area
            when {
                isLoading && hotelList.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            CircularProgressIndicator()
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = uiState,
                                style = MaterialTheme.typography.bodyLarge,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                hotelList.isNotEmpty() -> {
                    LazyColumn {
                        items(hotelList) { hotel ->
                            SwipeableHotelCard(
                                hotel = hotel,
                                onHotelSelected = onHotelSelected,
                                onEditHotel = onEditHotel,
                                viewModel = viewModel
                            )
                        }
                    }
                }
                else -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (searchText.isNotBlank()) "No hotels found matching '$searchText'" else "No hotels available",
                            style = MaterialTheme.typography.headlineMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }

        // Header with search bar
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colorScheme.surface)
                .padding(WindowInsets.statusBars.asPaddingValues())
                .height(120.dp) // Increased height to accommodate search bar
                .background(MaterialTheme.colorScheme.surface)
                .align(Alignment.TopCenter)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Top
            ) {
                // Top row with title and plus button
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp),
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
                        onClick = { onAddHotel() },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .testTag("HomeScreenPlusButton")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            tint = MaterialTheme.colorScheme.background
                        )
                    }
                }
                
                // Spacer(modifier = Modifier.height(8.dp))
                
                // Search bar in the header
                OutlinedTextField(
                    value = searchText,
                    onValueChange = { viewModel.onSearchTextChanged(it) },
                    placeholder = { 
                        Text(
                            "Search by Name",
                            color = Color.Gray,
                            fontSize = 16.sp
                        ) 
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp, vertical = 2.dp)
                        .testTag("SearchBar"),
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    textStyle = TextStyle(
                        fontSize = 16.sp,
                        color = Color.Black
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = Color.White,
                        focusedContainerColor = Color.White,
                        unfocusedBorderColor = Color.Transparent,
                        focusedBorderColor = Color.Transparent,
                        cursorColor = Color.Black,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                        focusedPlaceholderColor = Color.Gray,
                        unfocusedPlaceholderColor = Color.Gray
                    )
                )
            }
        }

        // Error handling
        Box(modifier = Modifier.fillMaxSize()) {
            errorMessage?.let { errorModel ->
                ErrorBanner(
                    error = errorModel,
                    onDismiss = { ErrorManager.clearError() }
                )
            }
            
            // ViewModel error state
            errorState?.let { error ->
                Snackbar(
                    modifier = Modifier.align(Alignment.BottomCenter),
                    action = {
                        TextButton(onClick = { viewModel.clearError() }) {
                            Text("Dismiss")
                        }
                    }
                ) {
                    Text(error)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SwipeableHotelCard(hotel: Hotel, onHotelSelected: (Hotel) -> Unit, onEditHotel: (Hotel) -> Unit,viewModel: HomeViewModel) {
    val dismissState = rememberDismissState()

    LaunchedEffect(dismissState.currentValue) {
        when {
            dismissState.isDismissed(DismissDirection.StartToEnd) -> {
                onEditHotel(hotel)
                dismissState.reset()
            }
            dismissState.isDismissed(DismissDirection.EndToStart) -> {
                viewModel.onDeleteHotel(hotel)
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
