package com.example.mepetcare.view.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mepetcare.data.local.TokenManager
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.model.ServiceType
import com.example.mepetcare.data.remote.DoctorApi
import com.example.mepetcare.data.remote.RetrofitClient
import com.example.mepetcare.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

// ADDED: tokenManager to the constructor
class DoctorViewModel(
    private val repository: DoctorRepository = DoctorRepository(),
    private val tokenManager: TokenManager
) : ViewModel() {

    private val DEFAULT_SERVICE_ID = 1 // 🔒 service default (internal)

    private val _owners = MutableStateFlow<List<Owner>>(emptyList())
    val owners = _owners.asStateFlow()

    private val _selectedPets = MutableStateFlow<List<Patient>>(emptyList())
    val selectedPets = _selectedPets.asStateFlow()

    fun loadOwners() {
        viewModelScope.launch {
            val response = repository.getOwners()
            if (response.isSuccessful) {
                _owners.value = response.body() ?: emptyList()
            }
        }
    }

    fun loadPets(ownerId: Int) {
        viewModelScope.launch {
            val response = repository.getOwnerPets(ownerId)
            if (response.isSuccessful) {
                _selectedPets.value = response.body() ?: emptyList()
            }
        }
    }

    fun saveExamination(
        patientId: Int,
        diagnosis: String,
        treatment: String,
        medication: String,
        notes: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val doctorId = tokenManager.getUserId()
            if (doctorId == -1) return@launch

            val data = mapOf(
                "idpasienk" to patientId.toString(),
                "idservice" to DEFAULT_SERVICE_ID.toString(),
                "iddoctor" to doctorId.toString(),
                "diagnosis" to diagnosis,
                "treatment" to treatment,
                "medication" to medication,
                "notes" to notes,
                "date" to SimpleDateFormat(
                    "yyyy-MM-dd",
                    Locale.getDefault()
                ).format(Date())
            )

            val response = repository.createMedicalRecord(data)
            if (response.isSuccessful) {
                onComplete()
            }
        }
    }
}



class DoctorViewModelFactory(private val context: android.content.Context) : androidx.lifecycle.ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return DoctorViewModel(
            repository = DoctorRepository(), // Added here
            tokenManager = TokenManager(context)
        ) as T
    }
}