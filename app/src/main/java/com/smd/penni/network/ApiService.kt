package com.smd.penni.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ApiService {
    // F1: Fetch market trends for the RecyclerView in Profile
    @GET("coins/markets")
    suspend fun getMarketTrends(
        @Query("vs_currency") currency: String = "usd",
        @Query("order") order: String = "market_cap_desc",
        @Query("per_page") perPage: Int = 15,
        @Query("page") page: Int = 1
    ): List<MarketCoin>

    // F1: Fetch a single price for the status bar in DataFragment
    @GET("simple/price")
    suspend fun getBitcoinPrice(
        @Query("ids") ids: String = "bitcoin",
        @Query("vs_currencies") currencies: String = "usd"
    ): Map<String, Map<String, Double>>
}

object RetrofitClient {
    private const val BASE_URL = "https://api.coingecko.com/api/v3/"

    val instance: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
