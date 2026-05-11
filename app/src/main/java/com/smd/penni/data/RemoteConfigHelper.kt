package com.smd.penni.data

import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import kotlinx.coroutines.tasks.await

object RemoteConfigHelper {
    private val remoteConfig = Firebase.remoteConfig

    init {
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }
        remoteConfig.setConfigSettingsAsync(configSettings)
        remoteConfig.setDefaultsAsync(mapOf(
            "welcome_message" to "Welcome to Penni!",
            "show_market_trends" to true
        ))
    }

    suspend fun fetchAndActivate(): Boolean {
        return remoteConfig.fetchAndActivate().await()
    }

    fun getWelcomeMessage(): String = remoteConfig.getString("welcome_message")
    fun isMarketTrendsEnabled(): Boolean = remoteConfig.getBoolean("show_market_trends")
}
