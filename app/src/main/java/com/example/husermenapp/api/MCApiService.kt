package com.example.husermenapp.api

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
    ): Response<List<MCProductWrapper>>

    @GET("categories/{category_id}")
    suspend fun getCategoryDetailsByIds(
        @Header("Authorization") authorization: String,
        @Path("category_id") categoryId: String
    ): Response<CategorySearchResponse>

    @GET("orders/search")
    suspend fun getOrders(
        @Query("seller") sellerId: String,
        @Header("Authorization") token: String,
        @Query("order.status") status: String = "paid",
        @Query("sort") sort: String = "date_desc",
        @Query("order") order: String = "desc"
    ): OrdersResponse
}