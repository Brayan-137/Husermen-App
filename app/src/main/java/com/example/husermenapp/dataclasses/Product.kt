package com.example.husermenapp.dataclasses

import java.io.Serializable

data class Product(
    var key: String? = null,
    var name: String? = null,
    var description: String? = null,
    var category: String? = null,
    var price: Int? = null,
    var stock: Int? = null,
    var imageUrl: String? = null
): Serializable