package com.example.lokeventapplication.view

import android.provider.CalendarContract.Events
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Star
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.lokeventapplication.model.Event
import com.example.lokeventapplication.viewmodel.AuthState
import com.example.lokeventapplication.viewmodel.AuthViewModel
import com.example.lokeventapplication.viewmodel.EventsViewModel
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder


@Composable
fun EventsScreen(
    modifier: Modifier = Modifier,
    navController: NavController,
    authViewModel: AuthViewModel,
    eventsViewModel: EventsViewModel = viewModel()
) {
    var showingMap by remember { mutableStateOf(false) }
    var filterText by remember { mutableStateOf("") }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    //dropdown
    val categories = listOf("Sve", "Koncert", "Sport", "Edukacija", "Kultura", "Izložba", "Ostalo")
    var selectedCategory by remember { mutableStateOf("Sve") }
    var expanded by remember { mutableStateOf(false) }


    val authState = authViewModel.authState.collectAsState()
    LaunchedEffect(authState.value) {
        if (authState.value is AuthState.Unauthenticated) {
            navController.navigate("login")
        }
    }
    val context = LocalContext.current
    val events by eventsViewModel.events.collectAsState()
    LaunchedEffect(Unit) {
        eventsViewModel.fetchEvents()
    }
    val filteredEvents = events.filter { event ->
        val matchesText = event.title.contains(filterText, ignoreCase = true)
        val matchesCategory = selectedCategory == "Sve" || event.category == selectedCategory
        val matchesFavorite = !showFavoritesOnly || event.isInterested
        matchesText && matchesCategory && matchesFavorite
    }

//    val filteredEvents = events.filter { event ->
//        val matchesText = event.title.contains(filterText, ignoreCase = true)
//        val matchesCategory = selectedCategory == "Sve" || event.category == selectedCategory
//        matchesText && matchesCategory
//    }

  //  val filteredEvents = events.filter {
  //      it.title.contains(filterText, ignoreCase = true)
  //  }
    @OptIn(ExperimentalMaterial3Api::class)
    (Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Lokalni događaji") },
                actions = {
                    IconButton(onClick = { showFavoritesOnly = !showFavoritesOnly }) {
                        Icon(
                            imageVector = if (showFavoritesOnly) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Prikaži samo favorite"
                        )
                    }

                    IconButton(onClick = {navController.navigate("add_event")}) {
                        Icon(imageVector = Icons.Default.Add,
                            contentDescription = "Dodaj događaj")
                    }
                    TextButton(onClick = { authViewModel.signout() }) {
                        Text("Odjava")
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
                label = { Text("Pretraži po nazivu") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = selectedCategory,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Filtriraj po kategoriji") },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp),
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    categories.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category) },
                            onClick = {
                                selectedCategory = category
                                expanded = false
                            }
                        )
                    }
                }
            }

            EventsList(events = filteredEvents,  navController = navController , onToggleInterest = {
                eventsViewModel.toggleInterest(it)
            })
            if (showingMap) {
              //  EventsMap(events = filteredEvents)
            } else {
                EventsList(events = filteredEvents, navController = navController, onToggleInterest = {
                    eventsViewModel.toggleInterest(it)
                })
            }


        }
    })
}
@Composable
fun EventsList(events: List<Event>,navController: NavController, modifier: Modifier = Modifier, onToggleInterest: (Event) -> Unit) {
    LazyColumn(modifier = modifier.fillMaxSize()) {
        items(events) { event ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable {
                    navController.navigate("event_detail/${event.id}")
                }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = event.title, style = MaterialTheme.typography.titleMedium)
                    Text(text = event.description, style = MaterialTheme.typography.bodyMedium)
                    Text(text = "Datum: ${event.date}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Kategorija: ${event.category}", style = MaterialTheme.typography.bodySmall)
                    Text(text = "Adresa: ${event.address}", style = MaterialTheme.typography.bodySmall)

                    IconButton(onClick = { onToggleInterest(event) }) {
                        Icon(
                            imageVector = if (event.isInterested) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Zanima me"
                        )
                    }
                }
            }
        }
    }
}
