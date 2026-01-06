package com.example.mepetcare.data.model

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class LoginResponse(
    val message: String,
    val token: String
)
