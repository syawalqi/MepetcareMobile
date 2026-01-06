package com.example.mepetcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mepetcare.view.login.LoginScreen
import com.example.mepetcare.view.doctor.DoctorHomeScreen
import com.example.mepetcare.data.remote.RetrofitClient
import com.example.mepetcare.view.admin.PatientListScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        RetrofitClient.init(applicationContext)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = "login"
            ) {
                composable("login") {
                    LoginScreen(
                        onLoginSuccess = { role ->
                            when (role) {
                                "admin" -> navController.navigate("admin") {
                                    popUpTo("login") { inclusive = true }
                                }
                                "doctor" -> navController.navigate("doctor") {
                                    popUpTo("login") { inclusive = true }
                                }
                            }
                        }
                    )
                }

                composable("admin") {
                    PatientListScreen()
                }

                composable("doctor") {
                    DoctorHomeScreen()
                }
            }
        }

    }
}