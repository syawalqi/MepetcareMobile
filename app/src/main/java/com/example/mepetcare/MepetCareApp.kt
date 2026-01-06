package com.example.mepetcare

import android.app.Application
import com.example.mepetcare.data.remote.RetrofitClient

class MepetCareApp : Application() {

    override fun onCreate() {
        super.onCreate()
        RetrofitClient.init(this)
    }
}
