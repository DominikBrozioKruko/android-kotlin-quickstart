package com.example.android_kotlin_quickstart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.android_kotlin_quickstart.data.model.Geo
import com.example.android_kotlin_quickstart.viewmodel.AddEditHotelViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHotelScreen(viewModel: AddEditHotelViewModel, navController: NavController) {
    val hotel by viewModel.hotel.collectAsState()
    val title by viewModel.title.collectAsState()
    val modelDidChange by viewModel.modelDidChange.collectAsState()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(title) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.secondary),
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            tint = MaterialTheme.colorScheme.secondary,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    TextButton(
                        onClick = {
                            viewModel.onAcceptButton()
                            navController.popBackStack()
                                  },
                        enabled = modelDidChange,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = MaterialTheme.colorScheme.primary,
                            disabledContentColor = MaterialTheme.colorScheme.tertiary
                        ),
                        modifier = Modifier.
                            testTag("addEditHotelButton")
                    ) {
                        Text(
                            text = "SAVE",
                            fontSize = 16.sp
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(scrollState)
                .background(MaterialTheme.colorScheme.background)
                .padding(16.dp)
        ) {
            Section {
                TextField(
                    value = hotel?.name.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(name = it) } },
                    label = { Text("Name") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("hotelNameTextField")
                )
                TextField(
                    value = hotel?.title.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(title = it) } },
                    label = { Text("Title") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
            Section {
                TextField(
                    value = hotel?.address.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(address = it) } },
                    label = { Text("Address") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.city.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(city = it) } },
                    label = { Text("City") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.state.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(state = it) } },
                    label = { Text("State") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.country.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(country = it) } },
                    label = { Text("Country") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.geo?.accuracy.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(geo = geo.copy(accuracy = it)) } },
                    label = { Text("Accuracy") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    TextField(
                        value = hotel?.geo?.lat?.toString().orEmpty(),
                        onValueChange = { latStr ->
                            val newLat = latStr.toDoubleOrNull()
                            if (newLat != null) {
                                viewModel.updateHotel {
                                    copy(geo = geo.copy(lat = newLat))
                                }
                            }
                        },
                        label = { Text("Latitude") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    TextField(
                        value = hotel?.geo?.lon?.toString().orEmpty(),
                        onValueChange = { lonStr ->
                            val newLon = lonStr.toDoubleOrNull()
                            if (newLon != null) {
                                viewModel.updateHotel {
                                    copy(geo = geo.copy(lon = newLon))
                                }
                            }
                        },
                        label = { Text("Longitude") },
                        colors = TextFieldDefaults.colors(
                            unfocusedContainerColor = Color.Transparent
                        ),
                        modifier = Modifier.weight(1f)
                    )
                }

                TextField(
                    value = hotel?.phone.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(phone = it) } },
                    label = { Text("Phone") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.tollfree.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(tollfree = it) } },
                    label = { Text("Toll-Free") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.email.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(email = it) } },
                    label = { Text("Email") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.fax.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(fax = it) } },
                    label = { Text("Fax") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.url.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(url = it) } },
                    label = { Text("Website") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.checkin.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(checkin = it) } },
                    label = { Text("Check-in Time") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.checkout.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(checkout = it) } },
                    label = { Text("Check-out Time") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                TextField(
                    value = hotel?.price.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(price = it) } },
                    label = { Text("Price") },
                    colors = TextFieldDefaults.colors(
                        unfocusedContainerColor = Color.Transparent
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
            Section {
                ToggleItem(
                    label = "Vacancy",
                    state = hotel?.vacancy == true,
                    onToggle = { isChecked ->
                        viewModel.updateHotel { copy(vacancy = isChecked) }
                    }
                )
                ToggleItem(
                    label = "Pets Allowed",
                    state = hotel?.petsOk == true,
                    onToggle = { isChecked ->
                        viewModel.updateHotel { copy(petsOk = isChecked) }
                    }
                )
                ToggleItem(
                    label = "Free Breakfast",
                    state = hotel?.freeBreakfast == true,
                    onToggle = { isChecked ->
                        viewModel.updateHotel { copy(freeBreakfast = isChecked) }
                    }
                )
                ToggleItem(
                    label = "Free Internet",
                    state = hotel?.freeInternet == true,
                    onToggle = { isChecked ->
                        viewModel.updateHotel { copy(freeInternet = isChecked) }
                    }
                )
                ToggleItem(
                    label = "Free Parking",
                    state = hotel?.freeParking == true,
                    onToggle = { isChecked ->
                        viewModel.updateHotel { copy(freeParking = isChecked) }
                    }
                )
            }
        }
    }
}

@Composable
fun Section(title: String? = null, content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()

    ) {
        title?.let { Text(text = it, fontWeight = FontWeight.Bold, fontSize = 16.sp, modifier = Modifier.padding(bottom = 8.dp)) }
        content()
    }
}

@Composable
fun ToggleItem(label: String, state: Boolean, onToggle: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label)
        Switch(checked = state, onCheckedChange = onToggle)
    }
}
