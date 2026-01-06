package com.example.mepetcare.data.repository

import com.example.mepetcare.data.model.LoginRequest
import com.example.mepetcare.data.model.LoginResponse
import com.example.mepetcare.data.remote.RetrofitClient
import retrofit2.Response

class AuthRepository {

    suspend fun loginAdmin(
        email: String,
        password: String
    ): Response<LoginResponse> {
        val request = LoginRequest(
            email = email,
            password = password
        )
        return RetrofitClient.authApi.loginAdmin(request)
    }


    suspend fun loginDoctor(
        email: String,
        password: String
    ): Response<LoginResponse> {
        val request = LoginRequest(email, password)
        return RetrofitClient.authApi.loginDoctor(request)
    }
}
