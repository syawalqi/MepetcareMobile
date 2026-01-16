package com.example.mepetcare.view.admin

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
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
    val success by viewModel.success.collectAsState() // ✅ FR-26

    var newPetName by remember { mutableStateOf("") }
    var newPetDate by remember { mutableStateOf("") }

    LaunchedEffect(owner.id) {
        viewModel.loadOwnerDetails(owner.id)
    }


    LaunchedEffect(success) {
        if (success != null) {
            delay(2000)
            viewModel.clearSuccess()
        }
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


            success?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
            }

            // --- OWNER INFO ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Owner: ${owner.name}", style = MaterialTheme.typography.titleMedium)
                    Text("Phone: ${owner.phone}")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))


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


            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            supportingContent = { Text("Admitted: ${pet.date_in}") },
                            trailingContent = {
                                TextButton(
                                    onClick = { viewModel.deletePatient(pet.id, owner.id) }
                                ) {
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
