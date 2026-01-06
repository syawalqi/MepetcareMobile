package com.example.mepetcare.view.patient

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient
import com.example.mepetcare.data.remote.RetrofitClient
import com.example.mepetcare.data.repository.OwnerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class PatientViewModel(
    private val repository: OwnerRepository = OwnerRepository()
) : ViewModel() {

    // These names must match what the UI calls
    private val _owners = MutableStateFlow<List<Owner>>(emptyList())
    val owners: StateFlow<List<Owner>> = _owners

    private val _loading = MutableStateFlow(false)
    val loading: StateFlow<Boolean> = _loading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    private val _selectedOwnerPets = MutableStateFlow<List<Patient>>(emptyList())
    val selectedOwnerPets: StateFlow<List<Patient>> = _selectedOwnerPets

    fun loadOwners() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null
            try {
                val response = repository.getOwners()
                if (response.isSuccessful) {
                    _owners.value = response.body() ?: emptyList()
                } else {
                    _error.value = "Error: ${response.code()}"
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun loadOwnerDetails(ownerId: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val response = repository.getOwnerPets(ownerId)
                if (response.isSuccessful) {
                    _selectedOwnerPets.value = response.body() ?: emptyList()
                }
            } catch (e: Exception) {
                _error.value = e.message
            } finally {
                _loading.value = false
            }
        }
    }


    fun deletePatient(patientId: Int, ownerId: Int) {
        viewModelScope.launch {
            try {
                // This calls your router.delete("/:id/delete")
                val response = RetrofitClient.ownerApi.deletePatient(patientId)
                if (response.isSuccessful) {
                    loadOwnerDetails(ownerId) // Refresh the list automatically
                }
            } catch (e: Exception) {
                _error.value = "Delete failed: ${e.message}"
            }
        }
    }

    fun addPatient(name: String, date: String, ownerId: Int, onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Using your exact backend keys: namak, rawatinapk, fk_iduserp
                val data = mapOf(
                    "namak" to name,
                    "rawatinapk" to date,
                    "fk_iduserp" to ownerId.toString()
                )
                val response = RetrofitClient.ownerApi.createPatient(data)
                if (response.isSuccessful) {
                    loadOwnerDetails(ownerId)
                    onComplete() // This clears the input fields in the UI
                }
            } catch (e: Exception) {
                _error.value = "Add failed: ${e.message}"
            }
        }
    }


    fun updatePatient(patientId: Int, name: String, date: String, ownerId: Int) {
        viewModelScope.launch {
            try {
                val data = mapOf(
                    "namak" to name,
                    "rawatinapk" to date,
                    "fk_iduserp" to ownerId.toString()
                )
                val response = RetrofitClient.ownerApi.updatePatient(patientId, data)
                if (response.isSuccessful) {
                    loadOwnerDetails(ownerId) // Refresh list
                }
            } catch (e: Exception) {
                _error.value = "Update failed: ${e.message}"
            }
        }
    }
}