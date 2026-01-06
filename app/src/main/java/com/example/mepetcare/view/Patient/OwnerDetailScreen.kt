package com.example.mepetcare.view.patient

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mepetcare.data.model.Owner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDetailScreen(
    owner: Owner,
    viewModel: PatientViewModel,
    onBack: () -> Unit
) {
    val pets by viewModel.selectedOwnerPets.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // Form State for Adding New Pet
    var newPetName by remember { mutableStateOf("") }
    var newPetDate by remember { mutableStateOf("") }

    LaunchedEffect(owner.id) {
        viewModel.loadOwnerDetails(owner.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(owner.name) },
                navigationIcon = {
                    TextButton(onClick = onBack) { Text("< Back") }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- SECTION 1: OWNER INFO ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "CONTACT INFO", style = MaterialTheme.typography.labelSmall)
                    Text(text = "Phone: ${owner.phone}", style = MaterialTheme.typography.bodyLarge)
                    Text(text = "Email: ${owner.email}", style = MaterialTheme.typography.bodyLarge)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: ADD NEW PET FORM (FR-20) ---
            Text(text = "ADD NEW PATIENT", style = MaterialTheme.typography.labelSmall)
            OutlinedTextField(
                value = newPetName,
                onValueChange = { newPetName = it },
                label = { Text("Pet Name") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = newPetDate,
                onValueChange = { newPetDate = it },
                label = { Text("Admission Date (YYYY-MM-DD)") },
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = {
                    if (newPetName.isNotEmpty() && newPetDate.isNotEmpty()) {
                        viewModel.addPatient(newPetName, newPetDate, owner.id) {
                            newPetName = ""
                            newPetDate = ""
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
            ) {
                Text("SAVE PATIENT")
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 3: PET LIST ---
            Text(text = "REGISTERED PETS", style = MaterialTheme.typography.headlineSmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (pets.isEmpty()) {
                Text("No pets registered for this owner.")
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pets) { pet ->
                        Card(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Row(
                                modifier = Modifier.padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(text = "🐾 ${pet.name}", style = MaterialTheme.typography.titleMedium)
                                    Text(text = "Admitted: ${pet.date_in ?: "N/A"}", style = MaterialTheme.typography.bodySmall)
                                }

                                // FR-25: Delete Patient
                                TextButton(onClick = { viewModel.deletePatient(pet.id, owner.id) }) {
                                    Text("DELETE", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}