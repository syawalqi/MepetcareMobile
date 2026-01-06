package com.example.mepetcare.view.doctor

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DoctorHomeScreen(viewModel: DoctorViewModel = viewModel()) {
    val owners by viewModel.owners.collectAsState()
    val pets by viewModel.selectedPets.collectAsState()
    var selectedOwner by remember { mutableStateOf<Owner?>(null) }

    // Popup states for Examination (FR-30)
    var showExamPopup by remember { mutableStateOf(false) }
    var currentPet by remember { mutableStateOf<Patient?>(null) }
    var keluhan by remember { mutableStateOf("") }
    var kondisi by remember { mutableStateOf("") }
    var rekomendasi by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { viewModel.loadOwners() }

    // --- FR-30: EXAMINATION POPUP ---
    if (showExamPopup && currentPet != null) {
        AlertDialog(
            onDismissRequest = { showExamPopup = false },
            title = { Text("Examine: ${currentPet!!.name}") },
            text = {
                Column {
                    OutlinedTextField(value = keluhan, onValueChange = { keluhan = it }, label = { Text("Keluhan (Complaints)") })
                    OutlinedTextField(value = kondisi, onValueChange = { kondisi = it }, label = { Text("Kondisi (Condition)") })
                    OutlinedTextField(value = rekomendasi, onValueChange = { rekomendasi = it }, label = { Text("Rekomendasi") })
                }
            },
            confirmButton = {
                Button(onClick = {
                    viewModel.saveExamination(currentPet!!.id, keluhan, kondisi, rekomendasi) {
                        showExamPopup = false
                        keluhan = ""; kondisi = ""; rekomendasi = ""
                    }
                }) { Text("SAVE RECORD") }
            }
        )
    }

    Scaffold(topBar = { TopAppBar(title = { Text("Doctor: Patient Search") }) }) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            if (selectedOwner == null) {
                // Step 1: List Owners
                LazyColumn {
                    items(owners) { owner ->
                        Card(modifier = Modifier.fillMaxWidth().clickable {
                            selectedOwner = owner
                            viewModel.loadPets(owner.id)
                        }.padding(vertical = 4.dp)) {
                            Text(owner.name, modifier = Modifier.padding(16.dp))
                        }
                    }
                }
            } else {
                // Step 2: List Pets with "PERIKSA" button
                TextButton(onClick = { selectedOwner = null }) { Text("< Back to Owners") }
                Text("Pets for ${selectedOwner!!.name}", style = MaterialTheme.typography.titleMedium)
                LazyColumn {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            trailingContent = {
                                Button(onClick = {
                                    currentPet = pet
                                    showExamPopup = true
                                }) { Text("PERIKSA") }
                            }
                        )
                    }
                }
            }
        }
    }
}
