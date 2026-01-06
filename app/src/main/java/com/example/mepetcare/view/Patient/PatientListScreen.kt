package com.example.mepetcare.view.patient

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mepetcare.data.model.Owner
import com.example.mepetcare.data.model.Patient

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PatientListScreen(
    viewModel: PatientViewModel = viewModel()
) {
    val owners by viewModel.owners.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.error.collectAsState()

    var selectedOwner by remember { mutableStateOf<Owner?>(null) }

    LaunchedEffect(Unit) {
        viewModel.loadOwners()
    }

    if (selectedOwner != null) {
        OwnerDetailView(
            owner = selectedOwner!!,
            viewModel = viewModel,
            onBack = { selectedOwner = null }
        )
    } else {
        Scaffold(
            topBar = { TopAppBar(title = { Text("Owners List") }) }
        ) { padding ->
            when {
                loading -> {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }
                error != null -> {
                    Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text(text = error!!, color = MaterialTheme.colorScheme.error)
                    }
                }
                else -> {
                    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding)) {
                        items(owners) { owner ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(8.dp)
                                    .clickable { selectedOwner = owner },
                                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                            ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                    Text(text = owner.name, style = MaterialTheme.typography.titleLarge)
                                    Text(text = "Phone: ${owner.phone}", style = MaterialTheme.typography.bodyMedium)
                                    Text(
                                        text = "View Pets →",
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.padding(top = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OwnerDetailView(
    owner: Owner,
    viewModel: PatientViewModel,
    onBack: () -> Unit
) {
    val pets by viewModel.selectedOwnerPets.collectAsState()
    val loading by viewModel.loading.collectAsState()

    // Create Form State (FR-20)
    var newPetName by remember { mutableStateOf("") }
    var newPetDate by remember { mutableStateOf("") }

    // Edit Pop-up State (FR-24)
    var showEditDialog by remember { mutableStateOf(false) }
    var editingPet by remember { mutableStateOf<Patient?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    LaunchedEffect(owner.id) {
        viewModel.loadOwnerDetails(owner.id)
    }

    // --- FR-24: THE EDIT POP-UP ---
    if (showEditDialog && editingPet != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Patient Info") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Patient Name") })
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(value = editDate, onValueChange = { editDate = it }, label = { Text("Date (YYYY-MM-DD)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    viewModel.updatePatient(editingPet!!.id, editName, editDate, owner.id)
                    showEditDialog = false
                }) { Text("UPDATE") }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("CANCEL") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Owner: ${owner.name}") },
                navigationIcon = { TextButton(onClick = onBack) { Text("< Back") } }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            // --- SECTION 1: FULL CREATE FORM (FR-20) ---
            Card(modifier = Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ADD NEW PATIENT", style = MaterialTheme.typography.labelLarge)
                    OutlinedTextField(value = newPetName, onValueChange = { newPetName = it }, label = { Text("Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPetDate, onValueChange = { newPetDate = it }, label = { Text("Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        if (newPetName.isNotBlank() && newPetDate.isNotBlank()) {
                            viewModel.addPatient(newPetName, newPetDate, owner.id) {
                                newPetName = ""; newPetDate = ""
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) { Text("SAVE PATIENT") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- SECTION 2: THE LIST (FR-22, 24, 25) ---
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text("🐾 ${pet.name}") },
                            supportingContent = { Text("In: ${pet.date_in}") },
                            trailingContent = {
                                Row {
                                    TextButton(onClick = {
                                        editingPet = pet
                                        editName = pet.name
                                        editDate = pet.date_in ?: ""
                                        showEditDialog = true
                                    }) { Text("EDIT") }

                                    TextButton(onClick = { viewModel.deletePatient(pet.id, owner.id) }) {
                                        Text("DEL", color = MaterialTheme.colorScheme.error)
                                    }
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