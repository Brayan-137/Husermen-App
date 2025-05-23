package com.example.husermenapp.api

import com.google.gson.annotations.SerializedName

data class ItemSearchResponse(
    val seller_id: String,
    val results: List<String>, // Lista de IDs de los items
    val paging: Paging
)

data class CategorySearchResponse(
    val id: String,
    val name: String
)

data class Paging(
    val total: Int,
    val offset: Int,
    val limit: Int
)

data class MCProductWrapper(
    val code: Int,
    val body: MCProductDetail
)

data class MCProductDetail(
    val id: String,
    val title: String,
    val price: Double,
    val category_id: String,
    val available_quantity: Int,
    val currency_id: String,
    val sold_quantity: Int,
    val thumbnail: String,
    val pictures: List<Picture>
)

data class Picture(
    val id: String,
    val url: String,
    val secure_url: String,
    val size: String,
    val max_size: String,
    val quality: String
)

data class OrdersResponse(
    val results: List<Order>
)

data class Order(
    val id: Long,
    @SerializedName("date_created") val dateCreated: String
)