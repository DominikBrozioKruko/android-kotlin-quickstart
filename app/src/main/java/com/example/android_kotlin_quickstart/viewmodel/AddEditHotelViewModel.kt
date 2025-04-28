package com.example.android_kotlin_quickstart.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class AddEditHotelViewModel : ViewModel() {

    private val _viewMode : ViewMode = ViewMode.Add
    var originalHotel: Hotel? = null
    val hotel = MutableStateFlow<Hotel?>(null)
    val title = MutableStateFlow<String>("")


    fun setViewMode(viewMode: ViewMode) {
        if (viewMode is ViewMode.Edit) {
            originalHotel = viewMode.hotel
            hotel.value = viewMode.hotel
            title.value = "Edit hotel"
        } else {
            originalHotel = null
            hotel.value = null
            title.value = "Add hotel"
        }
    }

    val modelDidChange: StateFlow<Boolean> = hotel
        .map { currentHotel -> currentHotel != originalHotel }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )
    fun onAcceptButton() {
//        when (viewMode) {
//            is ViewMode.Add -> DatabaseManager.addNewElement(hotel.toHotel())
//            is ViewMode.Edit -> DatabaseManager.updateExistingElement(hotel.toHotel())
//        }

    }

    fun updateHotel(update: Hotel.() -> Hotel) {
        hotel.value?.let {
            hotel.value = it.update()
        }
    }

}

// View mode sealed class
sealed class ViewMode {
    object Add : ViewMode()
    data class Edit(val hotel: Hotel) : ViewMode()
}