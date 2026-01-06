package com.example.mepetcare.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ServiceType(
    @Json(name = "id") val id: Int,
    @Json(name = "name") val name: String,
    @Json(name = "category") val category: String?,
    @Json(name = "price") val price: Double,
    @Json(name = "description") val description: String?
)