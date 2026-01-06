package com.example.mepetcare.data.local

import android.content.Context
import android.content.SharedPreferences
import android.util.Base64
import org.json.JSONObject

class TokenManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    /**
     * Saves the token and automatically extracts the user ID from the JWT payload
     */
    fun saveToken(token: String) {
        val editor = prefs.edit()
        editor.putString(KEY_TOKEN, token)

        // --- DECODE JWT TO GET USER ID ---
        try {
            val parts = token.split(".")
            if (parts.size == 3) {
                // The payload is the second part of the JWT
                val payload = String(Base64.decode(parts[1], Base64.DEFAULT))
                val json = JSONObject(payload)

                // Your backend uses { id: doctor.iddoctor } in the sign() function
                if (json.has("id")) {
                    val userId = json.getInt("id")
                    editor.putInt(KEY_USER_ID, userId)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        editor.apply()
    }

    fun getToken(): String? {
        return prefs.getString(KEY_TOKEN, null)
    }

    /**
     * Returns the doctor/user ID extracted during login.
     * Returns -1 if not found.
     */
    fun getUserId(): Int {
        return prefs.getInt(KEY_USER_ID, -1)
    }

    fun clearToken() {
        prefs.edit()
            .remove(KEY_TOKEN)
            .remove(KEY_USER_ID)
            .apply()
    }

    companion object {
        private const val PREFS_NAME = "auth_prefs"
        private const val KEY_TOKEN = "jwt_token"
        private const val KEY_USER_ID = "user_id"
    }
}