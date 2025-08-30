package com.example.lokeventapplication.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokeventapplication.viewmodel.AddEventViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    navController: NavController,
    viewModel: AddEventViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val categories = listOf("Koncert", "Sport", "Edukacija", "Kultura", "Izložba", "Ostalo")
    var expandedCategory by remember { mutableStateOf(false) }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Dodaj događaj") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = state.title,
                onValueChange = { viewModel.onValueChange("title", it) },
                label = { Text("Naslov") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.description,
                onValueChange = { viewModel.onValueChange("description", it) },
                label = { Text("Opis") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.date,
                onValueChange = { viewModel.onValueChange("date", it) },
                label = { Text("Datum (yyyy-MM-dd)") },
                modifier = Modifier.fillMaxWidth()
            )

            // Dropdown za kategoriju
            OutlinedTextField(
                value = state.category,
                onValueChange = {},
                readOnly = true,
                label = { Text("Kategorija") },
                modifier = Modifier.fillMaxWidth(),
                trailingIcon = {
                    IconButton(onClick = { expandedCategory = !expandedCategory }) {
                        Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                    }
                }
            )
            DropdownMenu(
                expanded = expandedCategory,
                onDismissRequest = { expandedCategory = false }
            ) {
                categories.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category) },
                        onClick = {
                            viewModel.onValueChange("category", category)
                            expandedCategory = false
                        }
                    )
                }
            }

            OutlinedTextField(
                value = state.address,
                onValueChange = { viewModel.onValueChange("address", it) },
                label = { Text("Adresa") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.latitude,
                onValueChange = { viewModel.onValueChange("latitude", it) },
                label = { Text("Latituda") },
                modifier = Modifier.fillMaxWidth()
            )
            OutlinedTextField(
                value = state.longitude,
                onValueChange = { viewModel.onValueChange("longitude", it) },
                label = { Text("Longituda") },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    viewModel.saveEvent(
                        onSuccess = { navController.popBackStack() },
                        onError = {
                            scope.launch { snackbarHostState.showSnackbar(it) }
                        }
                    )
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Spremi događaj")
            }
        }
    }
}
