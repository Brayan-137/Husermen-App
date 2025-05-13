package com.example.husermenapp

import java.io.Serializable

data class Tutorial (
    var name: String? = null,
    var description: String? = null,
    var matter: String? = null,
    var type: String? = null,
    var videoUrl: String? = null,
    var text: String? = null
): Serializable