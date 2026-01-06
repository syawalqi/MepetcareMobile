package com.example.mepetcare.data.remote

import android.content.Context
import com.example.mepetcare.data.local.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitClient {

    private const val BASE_URL = "http://192.168.100.6:3000/api/"

    private lateinit var retrofit: Retrofit

    fun init(context: Context) {
        val tokenManager = TokenManager(context)

        val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(AuthInterceptor(tokenManager))
            .build()

        retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
    }

    val authApi: AuthApi by lazy {
        retrofit.create(AuthApi::class.java)
    }

    val patientApi: OwnerApi by lazy {
        retrofit.create(OwnerApi::class.java)
    }

    val ownerApi: OwnerApi by lazy {
        retrofit.create(OwnerApi::class.java)
    }
}
