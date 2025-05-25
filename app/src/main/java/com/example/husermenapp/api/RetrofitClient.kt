package com.example.husermenapp.api

import TokenInterceptor
import TokenManager
import android.content.Context
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


class RetrofitClient {
    private val BASE_URL = "https://api.mercadolibre.com/"

    var tokenManager = TokenManager()

    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor(tokenManager))
        .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
        .build()

    val mcApiService: MCApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MCApiService::class.java)
    }
}