package com.smd.penni.network

data class ExchangeRateResponse(
    val base_code: String,
    val conversion_rates: Map<String, Double>
)
