package com.example.mepetcare.data.remote

import okhttp3.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DoctorApi {
    // To list owners and their pets
    @GET("userp/list")
    suspend fun getOwners(): Response<List<Owner>>

    @GET("userp/{id}/pasien")
    suspend fun getOwnerPets(@Path("id") id: Int): Response<List<Patient>>

    // FR-31: Save Medical Record (Pemeriksaan)
    @POST("pemeriksaan/create")
    suspend fun createExamination(@Body data: Map<String, String>): Response<Unit>
}