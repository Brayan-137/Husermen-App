package com.example.husermenapp.dataclasses

import java.io.Serializable

data class MCProduct (
    var key: String? = null,
    var name: String? = null,
    var price: Int? = null,
    var stock: Int? = null,
    var status: String? = null,
    var category: String? = null,
    var localProductKey: String? = null,
    var topSells: Int? = null,
): Serializable