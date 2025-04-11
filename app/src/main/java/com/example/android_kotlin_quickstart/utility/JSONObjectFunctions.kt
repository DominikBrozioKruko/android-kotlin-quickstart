package com.example.android_kotlin_quickstart.utility

import org.json.JSONObject


fun JSONObject.optNullableString(key: String): String? {
    val value = this.opt(key)
    return if (value == null || value == JSONObject.NULL) null else value.toString()
}