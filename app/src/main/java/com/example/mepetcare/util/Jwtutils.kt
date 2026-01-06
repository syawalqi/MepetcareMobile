package com.example.mepetcare.util

import android.util.Base64
import org.json.JSONObject

object JwtUtils {

    fun getRole(token: String): String? {
        return try {
            val parts = token.split(".")
            if (parts.size < 2) return null

            val payload = parts[1]
            val decodedBytes = Base64.decode(payload, Base64.URL_SAFE)
            val decodedPayload = String(decodedBytes)

            val json = JSONObject(decodedPayload)
            json.getString("role")
        } catch (e: Exception) {
            null
        }
    }
}
