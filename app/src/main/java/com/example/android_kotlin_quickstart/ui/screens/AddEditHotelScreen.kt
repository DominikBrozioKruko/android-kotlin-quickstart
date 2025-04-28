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
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
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
                            contentColor = Color.Blue,
                            disabledContentColor = Color.Gray
                        )
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
                .background(Color.White)
                .padding(16.dp)
        ) {
            Section {
                OutlinedTextField(
                    value = hotel?.name.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(name = it) } },
                    label = { Text("Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = hotel?.title.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(title = it) } },
                    label = { Text("Title") },
                    modifier = Modifier.fillMaxWidth()
                )
            }
            Spacer(Modifier.height(16.dp))
            Section {
                OutlinedTextField(
                    value = hotel?.address.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(address = it) } },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.city.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(city = it) } },
                    label = { Text("City") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.state.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(state = it) } },
                    label = { Text("State") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.country.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(country = it) } },
                    label = { Text("Country") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.geo?.accuracy.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(geo = geo.copy(accuracy = it)) } },
                    label = { Text("Accuracy") },
                    modifier = Modifier.fillMaxWidth()
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
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
                        modifier = Modifier.weight(1f)
                    )

                    OutlinedTextField(
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
                        modifier = Modifier.weight(1f)
                    )
                }

                OutlinedTextField(
                    value = hotel?.phone.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(phone = it) } },
                    label = { Text("Phone") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.tollfree.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(tollfree = it) } },
                    label = { Text("Toll-Free") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.email.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(email = it) } },
                    label = { Text("Email") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.fax.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(fax = it) } },
                    label = { Text("Fax") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.url.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(url = it) } },
                    label = { Text("Website") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.checkin.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(checkin = it) } },
                    label = { Text("Check-in Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.checkout.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(checkout = it) } },
                    label = { Text("Check-out Time") },
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = hotel?.price.orEmpty(),
                    onValueChange = { viewModel.updateHotel { copy(price = it) } },
                    label = { Text("Price") },
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
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFFF2F2F7))
            .padding(12.dp)

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
