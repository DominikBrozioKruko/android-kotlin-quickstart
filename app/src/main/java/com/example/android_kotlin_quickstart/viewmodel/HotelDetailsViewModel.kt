package com.example.android_kotlin_quickstart.viewmodel

import androidx.lifecycle.ViewModel
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class HotelDetailsViewModel : ViewModel() {

    private val _hotel = MutableStateFlow<Hotel?>(null)
    val hotel: StateFlow<Hotel?> = _hotel

    fun setHotel(hotel: Hotel) {
        _hotel.value = hotel
    }
}