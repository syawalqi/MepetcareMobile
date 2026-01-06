package com.example.mepetcare.view.doctor

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(
    viewModel: DoctorViewModel = viewModel(
        factory = DoctorViewModelFactory(LocalContext.current)
    )
) {
    val owners by viewModel.owners.collectAsState()
    val pets by viewModel.selectedPets.collectAsState()

    var selectedOwner by remember { mutableStateOf<Owner?>(null) }

    var showExamPopup by remember { mutableStateOf(false) }
    var currentPet by remember { mutableStateOf<Patient?>(null) }

    var diagnosis by remember { mutableStateOf("") }
    var treatment by remember { mutableStateOf("") }
    var medication by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        viewModel.loadOwners()
    }

    // ===== EXAMINATION POPUP =====
    if (showExamPopup && currentPet != null) {
        AlertDialog(
            onDismissRequest = { showExamPopup = false },
            title = { Text("Examine: ${currentPet!!.name}") },
            text = {
                Column(modifier = Modifier.verticalScroll(rememberScrollState())) {

                    OutlinedTextField(
                        value = diagnosis,
                        onValueChange = { diagnosis = it },
                        label = { Text("Diagnosis*") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = treatment,
                        onValueChange = { treatment = it },
                        label = { Text("Treatment*") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = medication,
                        onValueChange = { medication = it },
                        label = { Text("Medication") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    OutlinedTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        label = { Text("Additional Notes") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.saveExamination(
                            patientId = currentPet!!.id,
                            diagnosis = diagnosis,
                            treatment = treatment,
                            medication = medication,
                            notes = notes
                        ) {
                            showExamPopup = false
                            diagnosis = ""
                            treatment = ""
                            medication = ""
                            notes = ""
                        }
                    },
                    enabled = diagnosis.isNotBlank() && treatment.isNotBlank()
                ) {
                    Text("SAVE RECORD")
                }
            },
            dismissButton = {
                TextButton(onClick = { showExamPopup = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    // ===== MAIN SCREEN =====
    Scaffold(
        topBar = { TopAppBar(title = { Text("Doctor: Patient Search") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {

            if (selectedOwner == null) {
                LazyColumn {
                    items(owners) { owner ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    selectedOwner = owner
                                    viewModel.loadPets(owner.id)
                                }
                                .padding(vertical = 4.dp)
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(owner.name, style = MaterialTheme.typography.titleMedium)
                                Text("Phone: ${owner.phone}")
                            }
                        }
                    }
                }
            } else {
                TextButton(onClick = { selectedOwner = null }) {
                    Text("< Back to Owners")
                }

                Text(
                    "Pets for ${selectedOwner!!.name}",
                    style = MaterialTheme.typography.titleLarge
                )

                LazyColumn {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            supportingContent = {
                                Text("Admitted: ${pet.date_in ?: "N/A"}")
                            },
                            trailingContent = {
                                Button(onClick = {
                                    currentPet = pet
                                    diagnosis = ""
                                    treatment = ""
                                    medication = ""
                                    notes = ""
                                    showExamPopup = true
                                }) {
                                    Text("PERIKSA")
                                }
                            }
                        )
                        HorizontalDivider()
                    }
                }
            }
        }
    }
}

