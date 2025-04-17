package com.example.android_kotlin_quickstart

import com.example.android_kotlin_quickstart.data.model.AppError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object ErrorManager {
    private val _errorState = MutableStateFlow<AppError?>(null)
    val errorState: StateFlow<AppError?> = _errorState

    fun postError(message: AppError) {
        _errorState.value = message
    }

    fun clearError() {
        _errorState.value = null
    }
}