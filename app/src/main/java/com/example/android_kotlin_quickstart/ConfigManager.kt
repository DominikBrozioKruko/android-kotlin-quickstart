package com.example.android_kotlin_quickstart

import android.content.Context
import com.example.android_kotlin_quickstart.data.model.Config
import com.example.android_kotlin_quickstart.utility.optNullableString
import org.json.JSONObject
import java.io.InputStreamReader
import java.util.concurrent.atomic.AtomicReference

class ConfigManager private constructor() {

    private var context: Context? = null
    private var config: Config? = null

    // Initialize the context
    fun init(context: Context) {
        this.context = context
    }

    // Function to get the config, loading it if necessary
    fun getConfig(): Config? {
        if (config == null) {
            config = loadConfigFromAssets(context)
        }
        return config
    }

    // Load and parse the config from assets
    private fun loadConfigFromAssets(context: Context?): Config? {
        val json = try {
            val inputStream = context?.assets?.open("config.json")
            val reader = InputStreamReader(inputStream)
            reader.readText()
        } catch (e: Exception) {
            e.printStackTrace()
            "{}"
        }

        return parseConfig(json)
    }

    // Parse the JSON string into a Config object using org.json.JSONObject
    private fun parseConfig(configJson: String): Config? {
        return try {
            val jsonObject = JSONObject(configJson)
            val remoteCapellaEndpointURL = jsonObject.optNullableString("remoteCapellaEndpointURL")
            val userName = jsonObject.optNullableString("userName")
            val password = jsonObject.optNullableString("password")
            if (remoteCapellaEndpointURL != null && userName != null && password != null) {
                if(remoteCapellaEndpointURL.isNotEmpty() && userName.isNotEmpty() && password.isNotEmpty()) {
                    Config(remoteCapellaEndpointURL, userName, password)
                } else
                    null
            } else {
                null
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    companion object {

        private val INSTANCE = AtomicReference<ConfigManager?>()

        @Synchronized
        fun getInstance(context: Context): ConfigManager {
            var mgr = INSTANCE.get()
            if (mgr == null) {
                mgr = ConfigManager()
                if (INSTANCE.compareAndSet(null, mgr)) {
                    mgr.init(context)
                }
            }
            return INSTANCE.get()!!
        }
    }
}