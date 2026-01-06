package com.example.mepetcare.view.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mepetcare.data.local.TokenManager
import com.example.mepetcare.data.repository.AuthRepository
import com.example.mepetcare.util.JwtUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository(),
    private val tokenManager: TokenManager // Added to constructor
) : ViewModel() {


    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _loginSuccess = MutableStateFlow(false)
    val loginSuccess: StateFlow<Boolean> = _loginSuccess

    private val _userRole = MutableStateFlow<String?>(null)
    val userRole: StateFlow<String?> = _userRole

    // Removed initTokenManager(context)

    fun login(email: String, password: String) {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            _loginSuccess.value = false

            try {
                val adminResponse = repository.loginAdmin(email, password)

                if (adminResponse.isSuccessful && adminResponse.body() != null) {
                    val token = adminResponse.body()!!.token
                    tokenManager.saveToken(token)

                    _userRole.value = JwtUtils.getRole(token)
                    _loginSuccess.value = true
                    return@launch
                }

                val doctorResponse = repository.loginDoctor(email, password)

                if (doctorResponse.isSuccessful && doctorResponse.body() != null) {
                    val token = doctorResponse.body()!!.token
                    tokenManager.saveToken(token)

                    _userRole.value = JwtUtils.getRole(token)
                    _loginSuccess.value = true
                    return@launch
                }

                _error.value = "Invalid email or password"

            } catch (e: Exception) {
                _error.value = e.message ?: "Network error"
            } finally {
                _loading.value = false
            }
        }
    }
}

class LoginViewModelFactory(private val context: android.content.Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoginViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoginViewModel(
                repository = AuthRepository(),
                tokenManager = TokenManager(context)
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
