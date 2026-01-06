package com.example.mepetcare.view.doctor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.remote.DoctorApi
import com.example.mepetcare.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class DoctorViewModel(private val api: DoctorApi = RetrofitClient.doctorApi) : ViewModel() {
    private val _owners = MutableStateFlow<List<Owner>>(emptyList())
    val owners = _owners.asStateFlow()

    private val _selectedPets = MutableStateFlow<List<Patient>>(emptyList())
    val selectedPets = _selectedPets.asStateFlow()

    fun loadOwners() {
        viewModelScope.launch {
            val response = api.getOwners()
            if (response.isSuccessful) _owners.value = response.body() ?: emptyList()
        }
    }

    fun loadPets(ownerId: Int) {
        viewModelScope.launch {
            val response = api.getOwnerPets(ownerId)
            if (response.isSuccessful) _selectedPets.value = response.body() ?: emptyList()
        }
    }

    // FR-31 logic
    fun saveExamination(patientId: Int, keluhan: String, kondisi: String, rekomendasi: String, onComplete: () -> Unit) {
        viewModelScope.launch {
            val data = mapOf(
                "fk_idpasienk" to patientId.toString(),
                "keluhan" to keluhan,
                "kondisi" to kondisi,
                "rekomendasi" to rekomendasi
            )
            val response = api.createExamination(data)
            if (response.isSuccessful) onComplete()
        }
    }
}