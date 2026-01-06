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

    LaunchedEffect(owner.id) {
        viewModel.loadOwnerDetails(owner.id)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(owner.name) },
                navigationIcon = {
                    // Replaced IconButton/Icon with a simple TextButton
                    TextButton(onClick = onBack) {
                        Text("< Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Text("Owner Details", style = MaterialTheme.typography.labelLarge)
            Text("Email: ${owner.email}", style = MaterialTheme.typography.bodyLarge)
            Text("Phone: ${owner.phone}", style = MaterialTheme.typography.bodyLarge)

            Spacer(modifier = Modifier.height(24.dp))
            Text("Pets List", style = MaterialTheme.typography.headlineSmall)
            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            if (loading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (pets.isEmpty()) {
                Text("No pets found for this owner.", modifier = Modifier.padding(top = 16.dp))
            } else {
                LazyColumn {
                    items(pets) { pet ->
                        ListItem(
                            headlineContent = { Text(pet.name) },
                            supportingContent = { Text("Admitted: ${pet.date_in ?: "N/A"}") },
                            // Replaced Icon with a text emoji
                            leadingContent = { Text("🐾", style = MaterialTheme.typography.headlineSmall) },
                            colors = ListItemDefaults.colors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}