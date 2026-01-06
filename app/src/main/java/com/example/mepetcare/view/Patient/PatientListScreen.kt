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

    // Create Form State
    var newPetName by remember { mutableStateOf("") }
    var newPetDate by remember { mutableStateOf("") }

    // Edit Popup State
    var showEditDialog by remember { mutableStateOf(false) }
    var editingPet by remember { mutableStateOf<Patient?>(null) }
    var editName by remember { mutableStateOf("") }
    var editDate by remember { mutableStateOf("") }

    LaunchedEffect(owner.id) {
        viewModel.loadOwnerDetails(owner.id)
    }

    // --- FR-24: EDIT POPUP DIALOG ---
    if (showEditDialog && editingPet != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Patient") },
            text = {
                Column {
                    OutlinedTextField(value = editName, onValueChange = { editName = it }, label = { Text("Name") })
                    OutlinedTextField(value = editDate, onValueChange = { editDate = it }, label = { Text("Date (YYYY-MM-DD)") })
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    // This calls your PUT /pasienk/:id/update
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
        topBar = { TopAppBar(title = { Text(owner.name) }, navigationIcon = { TextButton(onClick = onBack) { Text("< Back") } }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp).fillMaxSize()) {

            // --- FR-20: FULL CREATE FORM ---
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("ADD PATIENT", style = MaterialTheme.typography.labelSmall)
                    OutlinedTextField(value = newPetName, onValueChange = { newPetName = it }, label = { Text("Pet Name") }, modifier = Modifier.fillMaxWidth())
                    OutlinedTextField(value = newPetDate, onValueChange = { newPetDate = it }, label = { Text("Admission Date (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth())
                    Button(onClick = {
                        if (newPetName.isNotEmpty()) {
                            viewModel.addPatient(newPetName, newPetDate, owner.id) {
                                newPetName = ""; newPetDate = ""
                            }
                        }
                    }, modifier = Modifier.fillMaxWidth()) { Text("SAVE") }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // --- LIST WITH EDIT/DELETE ---
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
            } else {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            supportingContent = { Text("Admitted: ${pet.date_in}") },
                            trailingContent = {
                                Row {
                                    // Open Edit Popup
                                    TextButton(onClick = {
                                        editingPet = pet
                                        editName = pet.name
                                        editDate = pet.date_in ?: ""
                                        showEditDialog = true
                                    }) { Text("EDIT") }

                                    // Delete
                                    TextButton(onClick = { viewModel.deletePatient(pet.id, owner.id) }) {
                                        Text("DEL", color = MaterialTheme.colorScheme.error)
                                    }
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}