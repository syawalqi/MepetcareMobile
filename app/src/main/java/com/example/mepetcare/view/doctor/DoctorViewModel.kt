package com.example.mepetcare.view.doctor

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mepetcare.data.local.TokenManager
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.model.MedicalRecord
import com.example.mepetcare.data.repository.DoctorRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*
import androidx.lifecycle.AndroidViewModel

class DoctorViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = DoctorRepository()
    private val tokenManager = TokenManager(application)

    private val DEFAULT_SERVICE_ID = 1

    // ===== OWNERS =====
    private val _owners = MutableStateFlow<List<Owner>>(emptyList())
    val owners = _owners.asStateFlow()

    // ===== PETS =====
    private val _selectedPets = MutableStateFlow<List<Patient>>(emptyList())
    val selectedPets = _selectedPets.asStateFlow()

    // ===== MEDICAL HISTORY (INI YANG KURANG TADI) =====
    private val _medicalHistory =
        MutableStateFlow<List<MedicalRecord>>(emptyList())
    val medicalHistory = _medicalHistory.asStateFlow()

    // ===== ERROR =====
    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    // ===== LOAD OWNERS =====
    fun loadOwners() {
        viewModelScope.launch {
            val response = repository.getOwners()
            if (response.isSuccessful) {
                _owners.value = response.body() ?: emptyList()
            }
        }
    }

    // ===== LOAD PETS =====
    fun loadPets(ownerId: Int) {
        viewModelScope.launch {
            val response = repository.getOwnerPets(ownerId)
            if (response.isSuccessful) {
                _selectedPets.value = response.body() ?: emptyList()
            }
        }
    }

    // ===== LOAD MEDICAL HISTORY =====
    fun loadMedicalHistory(patientId: Int) {
        viewModelScope.launch {
            val response = repository.getMedicalHistory(patientId)
            if (response.isSuccessful) {
                _medicalHistory.value = response.body() ?: emptyList()
            } else {
                _error.value = "Gagal memuat riwayat pemeriksaan"
            }
        }
    }

    // ===== SAVE EXAMINATION =====
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
            if (doctorId == -1) {
                _error.value = "Doctor ID tidak ditemukan"
                return@launch
            }

            val today = SimpleDateFormat(
                "yyyy-MM-dd",
                Locale.getDefault()
            ).format(Date())

            val data: Map<String, String> = mapOf(
                "idpasienk" to patientId.toString(),
                "iddoctor" to doctorId.toString(),
                "idservice" to DEFAULT_SERVICE_ID.toString(),
                "diagnosis" to diagnosis,
                "treatment" to treatment,
                "date" to today,
                "medication" to medication,
                "notes" to notes
            )

            val response = repository.createMedicalRecord(data)

            if (response.isSuccessful) {
                onComplete()
            } else {
                _error.value = "Gagal menyimpan data (${response.code()})"
            }
        }
    }
}
