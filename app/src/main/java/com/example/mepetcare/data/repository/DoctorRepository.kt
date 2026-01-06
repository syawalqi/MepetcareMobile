package com.example.mepetcare.data.repository

import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.model.ServiceType
import com.example.mepetcare.data.remote.DoctorApi
import com.example.mepetcare.data.remote.RetrofitClient
import retrofit2.Response

class DoctorRepository(private val api: DoctorApi = RetrofitClient.doctorApi) {

    suspend fun getOwners(): Response<List<Owner>> = api.getOwners()

    suspend fun getOwnerPets(ownerId: Int): Response<List<Patient>> = api.getOwnerPets(ownerId)

    suspend fun getServices(): Response<List<ServiceType>> = api.listServices()

    suspend fun createMedicalRecord(data: Map<String, String>): Response<Unit> =
        api.createMedicalRecord(data)
}