package com.example.android_kotlin_quickstart.viewmodel

import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.android_kotlin_quickstart.data.model.Hotel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import androidx.compose.runtime.*
import com.example.android_kotlin_quickstart.DBManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.lang.ref.WeakReference

class AddEditHotelViewModel(private val context: WeakReference<Context>) : ViewModel() {

    private var _viewMode: ViewMode = ViewMode.Add
    var originalHotel = MutableStateFlow<Hotel?>(null)
    val hotel = MutableStateFlow<Hotel?>(null)
    val title = MutableStateFlow<String>("")
    
    // Loading and error states
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _errorState = MutableStateFlow<String?>(null)
    val errorState: StateFlow<String?> = _errorState

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
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorState.value = null
                
                context.get()?.let { ctx ->
                    val dbManager = DBManager.getInstance(ctx)
                    when (_viewMode) {
                        is ViewMode.Add -> {
                            hotel.value?.let { 
                                dbManager.create(it)
                                Log.i(TAG, "Hotel created successfully: ${it.name}")
                            }
                        }
                        is ViewMode.Edit -> {
                            hotel.value?.let { updatedHotel ->
                                (_viewMode as ViewMode.Edit).hotel.value = updatedHotel
                                dbManager.update(updatedHotel)
                                Log.i(TAG, "Hotel updated successfully: ${updatedHotel.name}")
                            }
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error saving hotel", e)
                _errorState.value = "Failed to save hotel: ${e.localizedMessage}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun updateHotel(update: Hotel.() -> Hotel) {
        hotel.value?.let {
            hotel.value = it.update()
        }
    }
    
    fun clearError() {
        _errorState.value = null
    }
    
    companion object {
        private const val TAG = "AddEditHotelViewModel"
    }
}

// View mode sealed class
sealed class ViewMode {
    object Add : ViewMode()
    data class Edit(val hotel: MutableStateFlow<Hotel?>) : ViewMode()
}