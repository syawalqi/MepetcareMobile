package com.example.mepetcare.data.repository

import com.example.mepetcare.data.remote.RetrofitClient

class OwnerRepository {
    suspend fun getOwners() = RetrofitClient.ownerApi.getOwners()

    suspend fun getOwnerPets(ownerId: Int) = RetrofitClient.patientApi.getOwnerPets(ownerId)
}