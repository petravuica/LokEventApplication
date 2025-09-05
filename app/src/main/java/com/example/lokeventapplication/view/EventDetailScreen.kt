package com.example.lokeventapplication.view

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.lokeventapplication.R
import com.example.lokeventapplication.viewmodel.EventsViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun EventDetailScreen(
    eventId: String,
    eventsViewModel: EventsViewModel,
    navController: NavController,
    context: Context
) {
    val events by eventsViewModel.events.collectAsState() //podaci se odmah updejtaju
    val event = events.find { it.id == eventId }

    var showDeleteDialog by remember { mutableStateOf(false) }

    if (event == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Događaj nije pronađen")
        }
    } else {
        val scrollState = rememberScrollState()


        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Naslov
            Text(
                text = event.title,
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(top = 48.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Info kartice
            InfoRow(label = stringResource(R.string.label_category), value = event.category)
            InfoRow(label = stringResource(R.string.date), value = event.date)
            InfoRow(label = stringResource(R.string.address), value = event.address)
            Spacer(modifier = Modifier.height(24.dp))

            // Opis
            Text(
                text = event.description,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Gumb za interese
            Button(
                onClick = { eventsViewModel.toggleInterest(event) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(if (event.isInterested) stringResource(R.string.delete_favourites) else stringResource(R.string.add_favourites))
            }

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { showDeleteDialog = true },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.delete_event), color = MaterialTheme.colorScheme.onError)
            }
            Spacer(modifier = Modifier.height(24.dp))


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


            Spacer(modifier = Modifier.height(16.dp))

            if (showDeleteDialog) {
                AlertDialog(
                    onDismissRequest = { showDeleteDialog = false },
                    title = { Text(stringResource(R.string.delete_confirmation_title)) },
                    text = { Text(stringResource(R.string.delete_confirmation_message)) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                eventsViewModel.deleteEvent(
                                    event.id,
                                    onSuccess = { navController.popBackStack() },
                                    onError = { message ->
                                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                                    }
                                )
                                showDeleteDialog = false
                            }
                        ) { Text(stringResource(R.string.yes)) }
                    },
                    dismissButton = {
                        TextButton(onClick = { showDeleteDialog = false }) { Text(stringResource(R.string.no)) }
                    }
                )
            }
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.Center
    ) {
        Text(
            text = "$label: ",
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
            color = MaterialTheme.colorScheme.primary
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}