package com.example.husermenapp

import java.util.UUID

data class Item(
    var name: String? = null,
    var description: String? = null,
    var price: Int = 0,
    var stock: Int = 0,
    val uid: String? = UUID.randomUUID().toString()
) {
    init {
        name = name?.lowercase()
    }
}