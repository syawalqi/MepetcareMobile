package com.example.mepetcare.data.remote

import com.example.mepetcare.data.model.LoginRequest
import com.example.mepetcare.data.model.LoginResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthApi {

    @POST("admin/login")
    suspend fun loginAdmin(
        @Body request: LoginRequest
    ): Response<LoginResponse>

    @POST("doctorAuth/login")
    suspend fun loginDoctor(
        @Body request: LoginRequest
    ): Response<LoginResponse>
}

