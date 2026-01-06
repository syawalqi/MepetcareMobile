package com.example.mepetcare.data.remote

import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface OwnerApi {

    @GET("userp/list")
    suspend fun getOwners(): Response<List<Owner>>

    @GET("userp/{id}/pasien")
    suspend fun getOwnerPets(@Path("id") id: Int): Response<List<Patient>>

    @POST("pasienk/create")
    suspend fun createPatient(@Body data: Map<String, String>): Response<Unit>

    @DELETE("pasienk/{id}/delete")
    suspend fun deletePatient(@Path("id") id: Int): Response<Unit>



}