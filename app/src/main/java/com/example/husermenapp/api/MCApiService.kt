package com.example.husermenapp.api

import com.example.husermenapp.dataclasses.ItemSearchResponse
import com.example.husermenapp.dataclasses.ProductDetail
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

interface MCApiService {

    @GET("users/{user_id}/items/search")
    suspend fun getUserItems(
        @Path("user_id") userId: String,
        @Header("Authorization") authorization: String, // "Bearer YOUR_ACCESS_TOKEN"
        @Query("offset") offset: Int,
        @Query("limit") limit: Int
    ): Response<ItemSearchResponse>

    @GET("items")
    suspend fun getProductDetailsByIds(
        @Header("Authorization") authorization: String,
        @Query("ids") itemIds: String // IDs separated by commas: "MCO123,MCO456"
    ): Response<List<ProductDetail>>
}