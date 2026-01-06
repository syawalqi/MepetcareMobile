package com.example.mepetcare.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.view.admin.PatientViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDetailScreen(
    owner: Owner,
    viewModel: PatientViewModel,
    onBack: () -> Unit
) {
    val pets by viewModel.selectedOwnerPets.collectAsState()
    val loading by viewModel.loading.collectAsState()

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
        // We use a Column and ensure the List takes up the remaining space
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // --- OWNER INFO ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Owner: ${owner.name}", style = MaterialTheme.typography.titleMedium)
                    Text("Phone: ${owner.phone}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- ADD FORM (FR-20) ---
            // Putting the form in a Card so it's clearly visible
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ADD NEW PATIENT", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(
                        value = newPetName,
                        onValueChange = { newPetName = it },
                        label = { Text("Pet Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = newPetDate,
                        onValueChange = { newPetDate = it },
                        label = { Text("Date (YYYY-MM-DD)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Button(
                        onClick = {
                            if (newPetName.isNotEmpty()) {
                                viewModel.addPatient(newPetName, newPetDate, owner.id) {
                                    newPetName = ""
                                    newPetDate = ""
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("SAVE PATIENT")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("PET LIST", style = MaterialTheme.typography.titleSmall)

            // --- THE LIST (FR-22 & FR-25) ---
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                // The .weight(1f) ensures this list fills the rest of the screen
                // but doesn't hide the form above it.
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            supportingContent = { Text("Admitted: ${pet.date_in}") },
                            trailingContent = {
                                // DELETE BUTTON (FR-25)
                                TextButton(onClick = { viewModel.deletePatient(pet.id, owner.id) }) {
                                    Text("DELETE", color = MaterialTheme.colorScheme.error)
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