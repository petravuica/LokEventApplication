package com.example.lokeventapplication.view

import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.example.lokeventapplication.viewmodel.EventsViewModel
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.material3.*
import androidx.compose.ui.Alignment
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.android.compose.*
import com.google.android.gms.maps.model.LatLng

@Composable
fun EventDetailScreen(eventId: String, eventsViewModel: EventsViewModel) {
    val events by eventsViewModel.events.collectAsState()
    val event = events.find { it.id == eventId }

    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Događaj nije pronađen")
        }
    } else {
        val scrollState = rememberScrollState()

        @OptIn(ExperimentalMaterial3Api::class)
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(event.title) }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
            ) {
                Text(
                    text = event.title,
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Kategorija: ${event.category}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Datum: ${event.date}", style = MaterialTheme.typography.bodyMedium)
                Text(text = "Adresa: ${event.address}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = event.description, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { eventsViewModel.toggleInterest(event) },
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Text(if (event.isInterested) "Makni iz interesa" else "Dodaj u interese")
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 📍 Mapa
                if (event.location != null) {
                    val eventLocation = LatLng(
                        event.location.latitude,
                        event.location.longitude
                    )
                    val cameraPositionState = rememberCameraPositionState {
                        position = CameraPosition.fromLatLngZoom(eventLocation, 14f)
                    }

                    GoogleMap(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp),
                        cameraPositionState = cameraPositionState
                    ) {
                        Marker(
                            state = MarkerState(position = eventLocation),
                            title = event.title,
                            snippet = event.address
                        )
                    }
                } else {
                    Text("Lokacija nije dostupna")
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}
