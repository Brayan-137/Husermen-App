package com.example.husermenapp

import java.io.Serializable

data class Item(
    var key: String? = null,
    var name: String? = null,
    var description: String? = null,
    var category: String? = null,
    var price: Int = 0,
    var stock: Int = 0,
): Serializable {
    init {
        name = name?.lowercase()
    }
}