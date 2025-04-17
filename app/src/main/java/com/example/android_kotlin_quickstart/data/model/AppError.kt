package com.example.android_kotlin_quickstart.data.model

data class AppError(
    val title: String = "",
    val message: String = "",
    val showDismissButton: Boolean = true
)