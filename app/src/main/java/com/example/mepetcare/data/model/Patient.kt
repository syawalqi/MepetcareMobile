package com.example.mepetcare.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class Patient(
    val id: Int,        // mapped from idpasienk
    val name: String,    // mapped from namak
    val date_in: String? // mapped from rawatinapk
)