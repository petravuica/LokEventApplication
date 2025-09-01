package com.example.lokeventapplication.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
//import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokeventapplication.model.Event
import com.example.lokeventapplication.viewmodel.AuthState
import com.example.lokeventapplication.viewmodel.AuthViewModel
import com.example.lokeventapplication.viewmodel.EventsViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import com.example.lokeventapplication.R


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    eventsViewModel: EventsViewModel = viewModel()
) {
    var filterText by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    //val categories = listOf("Sve", "Koncert", "Sport", "Edukacija", "Kultura", "Izložba", "Ostalo")
    val categoryIds = listOf(
        R.string.category_all,
        R.string.category_concert,
        R.string.category_sport,
        R.string.category_education,
        R.string.category_culture,
        R.string.category_exhibition,
        R.string.category_other
    )

    var selectedCategory by remember { mutableStateOf(R.string.category_all) }
    var expanded by remember { mutableStateOf(false) }

    val authState = authViewModel.authState.collectAsState()
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }

    val events by eventsViewModel.events.collectAsState()
    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents()
    }

    val filteredEvents = events.filter { event ->
        val matchesText = event.title.contains(filterText, ignoreCase = true)
        val matchesCategory = selectedCategory == R.string.category_all || event.category == stringResource(selectedCategory)
        val matchesFavorite = !showFavoritesOnly || event.isInterested
        matchesText && matchesCategory && matchesFavorite
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(
                    R.string.events_title),
                    color = MaterialTheme.colorScheme.onPrimary) },
                modifier = Modifier.statusBarsPadding(),
                colors = TopAppBarDefaults.mediumTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                actions = {
                    IconButton(onClick = { showFavoritesOnly = !showFavoritesOnly }) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = stringResource(R.string.favorites_only),
                            tint = if (showFavoritesOnly) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    IconButton(onClick = { navController.navigate("add_event") }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add_event),
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }

                    TextButton(onClick = { authViewModel.signout() }) {
                        Text(stringResource(R.string.logout), color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            )
        },
        modifier = modifier.fillMaxSize()
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            OutlinedTextField(
                value = filterText,
                onValueChange = { filterText = it },
                label = { Text(stringResource(R.string.search_label)) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                    cursorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                shape = MaterialTheme.shapes.medium
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }

            ) {
                OutlinedTextField(
                    value = stringResource(selectedCategory),
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.category_filter_label)) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .align(Alignment.CenterHorizontally),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedTextColor = MaterialTheme.colorScheme.primary,
                        focusedTrailingIconColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false },
                    modifier = Modifier.fillMaxWidth()// centriranje
                ) {
                    categoryIds.forEach { categoryResId ->
                        DropdownMenuItem(
                            text = { Text(stringResource(categoryResId)) },
                            onClick = {
                                selectedCategory = categoryResId
                                expanded = false
                            },
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))


                EventsList(
                    events = filteredEvents,
                    navController = navController,
                    onToggleInterest = { eventsViewModel.toggleInterest(it) }
                )

        }
    }
}

@Composable
fun EventsList(
    events: List<Event>,
    navController: NavController,
    modifier: Modifier = Modifier,
    onToggleInterest: (Event) -> Unit
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 12.dp)
    ) {
        items(events) { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { navController.navigate("event_detail/${event.id}") },
                shape = MaterialTheme.shapes.medium,
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = event.title,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column {
                            Text("${stringResource(R.string.date)}: ${event.date}", style = MaterialTheme.typography.bodySmall)
                            Text("${stringResource(R.string.category)}: ${event.category}", style = MaterialTheme.typography.bodySmall)
                            Text("${stringResource(R.string.address)}: ${event.address}\"", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = { onToggleInterest(event) }) {
                            Icon(
                                imageVector = if (event.isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                                contentDescription = stringResource(R.string.interested),
                                tint = if (event.isInterested) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

