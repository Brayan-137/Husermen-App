package com.example.husermenapp.dataclasses

data class ItemSearchResponse(
    val seller_id: String,
    val results: List<String>, // Lista de IDs de los items
    val paging: Paging
)

data class Paging(
    val total: Int,
    val offset: Int,
    val limit: Int
)
data class ProductDetail(
    val id: String,
    val title: String,
    val price: Double,
    val currency_id: String,
    val thumbnail: String, // Miniatura de la imagen principal
    val pictures: List<Picture> // Lista de URLs de las imágenes
)

data class Picture(
    val id: String,
    val url: String,
    val secure_url: String,
    val size: String,
    val max_size: String,
    val quality: String
)
