package com.example.husermenapp.dataclasses

import java.io.Serializable

data class MCProduct (
    var id: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var stock: Int? = null,
    var status: String? = null,
    var category: String? = null,
    var topSells: Int? = null,
    var imageUrl: String? = null
): Serializable