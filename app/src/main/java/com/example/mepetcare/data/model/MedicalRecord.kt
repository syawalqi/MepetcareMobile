package com.example.mepetcare.data.model

import com.squareup.moshi.Json

/**
 * Represents the cleaned data returned by the backend
 * in getRecordsByPatient (Controller #2)
 */
data class MedicalRecord(
    @Json(name = "id") val id: Int,
    @Json(name = "date") val date: String,
    @Json(name = "patient") val patientName: String,
    @Json(name = "service") val serviceName: String,
    @Json(name = "category") val category: String?,
    @Json(name = "doctor") val doctorName: String,
    @Json(name = "diagnosis") val diagnosis: String,
    @Json(name = "treatment") val treatment: String,
    @Json(name = "medication") val medication: String?,
    @Json(name = "notes") val notes: String?
)

/**
 * Data class to represent the body for creating a record
 * Matches the 'req.body' structure in createMedicalRecord (Controller #1)
 */
data class MedicalRecordRequest(
    val idpasienk: Int,
    val idservice: Int,
    val iddoctor: Int,
    val diagnosis: String,
    val treatment: String,
    val medication: String?,
    val notes: String?,
    val date: String // Backend requires YYYY-MM-DD format
)