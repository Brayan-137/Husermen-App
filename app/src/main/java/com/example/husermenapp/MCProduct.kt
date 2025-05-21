package com.example.husermenapp

import java.io.Serializable

data class MCProduct (
    var key: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var stock: Int? = null,
    var status: String? = null
): Serializable