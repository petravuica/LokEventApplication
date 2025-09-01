package com.example.lokeventapplication.view

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokeventapplication.viewmodel.AddEventViewModel
import kotlinx.coroutines.launch
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.lokeventapplication.R

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

  //  val categories = listOf("Koncert", "Sport", "Edukacija", "Kultura", "Izložba", "Ostalo")
    val categories = listOf(
        R.string.category_concert,
        R.string.category_sport,
        R.string.category_education,
        R.string.category_culture,
        R.string.category_exhibition,
        R.string.category_other
    )


    var expandedCategory by remember { mutableStateOf(false) }

    Scaffold(

        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.add_event),
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Natrag",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
                .fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Card(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    OutlinedTextField(
                        value = state.title,
                        onValueChange = { viewModel.onValueChange("title", it) },
                        label = { Text(stringResource(R.string.add_event_title)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            cursorColor = MaterialTheme.colorScheme.primary,
                            focusedLabelColor = MaterialTheme.colorScheme.primary,
                            unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    OutlinedTextField(
                        value = state.description,
                        onValueChange = { viewModel.onValueChange("description", it) },
                        label = { Text(stringResource(R.string.label_description)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    OutlinedTextField(
                        value = state.date,
                        onValueChange = { viewModel.onValueChange("date", it) },
                        label = { Text(stringResource(R.string.date)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors()
                    )

                    OutlinedTextField(
                        value = state.category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text(stringResource(R.string.category)) },
                        modifier = Modifier.fillMaxWidth(),
                        trailingIcon = {
                            IconButton(onClick = { expandedCategory = !expandedCategory }) {
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null)
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    DropdownMenu(
                        expanded = expandedCategory,
                        onDismissRequest = { expandedCategory = false }
                    ) {
                        categories.forEach { categoryId ->
                            val categoryText = stringResource(categoryId)
                            DropdownMenuItem(
                                text = {  Text(categoryText) },
                                onClick = {
                                    viewModel.onValueChange("category", categoryText)
                                    expandedCategory = false
                                }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = state.address,
                        onValueChange = { viewModel.onValueChange("address", it) },
                        label = { Text(stringResource(R.string.address)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors()
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        OutlinedTextField(
                            value = state.latitude,
                            onValueChange = { viewModel.onValueChange("latitude", it) },
                            label = { Text(stringResource(R.string.label_latitude)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                        OutlinedTextField(
                            value = state.longitude,
                            onValueChange = { viewModel.onValueChange("longitude", it) },
                            label = { Text(stringResource(R.string.label_longitude)) },
                            modifier = Modifier.weight(1f),
                            colors = OutlinedTextFieldDefaults.colors()
                        )
                    }
                    val context = LocalContext.current
                    Button(
                        onClick = {
                            viewModel.saveEvent(
                                context = context,
                                onSuccess = { navController.popBackStack() },
                                onError = { scope.launch { snackbarHostState.showSnackbar(it) } }
                            )
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        )
                    ) {
                        Text(stringResource(R.string.button_save_event))
                    }
                }
            }
        }
    }
}
