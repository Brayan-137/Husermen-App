package com.example.husermenapp

import java.io.Serializable

data class Tutorial (
    var key: String? = null,
    var name: String? = null,
    var description: String? = null,
    var topic: String? = null,
    var type: String? = null,
    var imageUrl: String? = null,
    var content: String? = null
): Serializable
