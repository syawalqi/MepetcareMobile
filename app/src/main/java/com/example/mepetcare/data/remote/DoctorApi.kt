package com.example.mepetcare.data.remote

import com.example.mepetcare.data.model.MedicalRecord
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.model.ServiceType
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface DoctorApi {
    @GET("userp/list")
    suspend fun getOwners(): Response<List<Owner>>

    @GET("userp/{id}/pasien")
    suspend fun getOwnerPets(@Path("id") id: Int): Response<List<Patient>>

    // NEW: Get historical records
    @GET("medical-records/pasien/{idpasienk}")
    suspend fun getMedicalHistory(@Path("idpasienk") id: Int): Response<List<MedicalRecord>>

    // NEW: Create record (Matches your controller)
    @POST("medical-records/create")
    suspend fun createMedicalRecord(@Body data: Map<String, String>): Response<Unit>

    // Inside DoctorApi interface
    @GET("services/list") // Path based on your router export
    suspend fun listServices(): retrofit2.Response<List<ServiceType>>

}