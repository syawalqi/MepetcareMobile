package com.example.mepetcare.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Owner(
    val id: Int,
    val name: String,
    val email: String,
    val phone: String
)