package com.smd.penni.network

data class MarketCoin(
    val name: String,
    val symbol: String,
    val current_price: Double,
    val price_change_percentage_24h: Double
)
