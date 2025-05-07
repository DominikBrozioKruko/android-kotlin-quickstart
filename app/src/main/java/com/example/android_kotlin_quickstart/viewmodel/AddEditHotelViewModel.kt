package com.example.android_kotlin_quickstart.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.*
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_quickstart.DBManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import java.lang.ref.WeakReference
import kotlin.random.Random

class AddEditHotelViewModel(private val context: WeakReference<Context>) : ViewModel() {

    init {

    }

    private var _viewMode : ViewMode = ViewMode.Add
    var originalHotel = MutableStateFlow<Hotel?>(null)
    val hotel = MutableStateFlow<Hotel?>(null)
    val title = MutableStateFlow<String>("")


    fun setViewMode(viewMode: ViewMode) {
        _viewMode = viewMode
        if (viewMode is ViewMode.Edit) {
            originalHotel = viewMode.hotel
            hotel.value = viewMode.hotel.value
            title.value = "Edit hotel"
        } else {
            val newHotel = Hotel.empty()
            originalHotel.value = newHotel
            hotel.value = newHotel
            title.value = "Add hotel"
        }
    }

    val modelDidChange: StateFlow<Boolean> = hotel
        .map { currentHotel -> currentHotel != originalHotel.value }
        .stateIn(
            viewModelScope,
            SharingStarted.Eagerly,
            false
        )
    fun onAcceptButton() {
        context.get()?.let { context ->
            val dbManager = DBManager.getInstance(context)
            when (_viewMode) {
                is ViewMode.Add -> hotel.value?.let { dbManager.create(it) }
                is ViewMode.Edit -> hotel.value?.let {
                    (_viewMode as ViewMode.Edit).hotel.value = it
                    dbManager.update(it) }
            }
        }

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
    data class Edit(val hotel: MutableStateFlow<Hotel?>) : ViewMode()
}