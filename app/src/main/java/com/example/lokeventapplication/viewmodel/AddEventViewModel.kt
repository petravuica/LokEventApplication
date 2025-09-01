package com.example.lokeventapplication.viewmodel


import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.Firebase
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import com.example.lokeventapplication.model.EventUiState
import androidx.work.*
import com.example.lokeventapplication.notifications.NotificationWorker
import java.time.LocalDateTime
import java.time.LocalTime
import java.util.concurrent.TimeUnit

class AddEventViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _uiState = MutableStateFlow(EventUiState())
    val uiState: StateFlow<EventUiState> = _uiState

    fun onValueChange(field: String, value: String) {
        _uiState.value = when (field) {
            "title" -> _uiState.value.copy(title = value)
            "description" -> _uiState.value.copy(description = value)
            "date" -> _uiState.value.copy(date = value)
            "category" -> _uiState.value.copy(category = value)
            "address" -> _uiState.value.copy(address = value)
            "latitude" -> _uiState.value.copy(latitude = value)
            "longitude" -> _uiState.value.copy(longitude = value)
            else -> _uiState.value
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun saveEvent(context: Context, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val state = _uiState.value
        try {
            val parsedDate = LocalDate.parse(state.date)
            val lat = state.latitude.toDouble()
            val lon = state.longitude.toDouble()

            val event = hashMapOf(
                "title" to state.title,
                "description" to state.description,
                "date" to parsedDate.toString(),
                "category" to state.category,
                "address" to state.address,
                "location" to GeoPoint(lat, lon)
            )

            viewModelScope.launch {
                db.collection("events")
                    .add(event)
                    .addOnSuccessListener {
                        scheduleEventReminder(context, state.title, parsedDate.toString())
                        onSuccess()
                    }
                    .addOnFailureListener { onError("Greška pri spremanju: ${it.message}") }
            }
        } catch (e: DateTimeParseException) {
            onError("Datum mora biti u formatu yyyy-MM-dd.")
        } catch (e: Exception) {
            onError("Greška: ${e.message}")
        }

    }
    @RequiresApi(Build.VERSION_CODES.O)
        private fun scheduleEventReminder(context: Context, title: String, date: String) {
            val eventDate = LocalDate.parse(date)
            val reminderDateTime = LocalDateTime.of(eventDate, LocalTime.of(15, 52)) // 08:00 ujutro

            val delay = java.time.Duration.between(
                LocalDateTime.now(),
                reminderDateTime
            ).toMillis()

            if (delay > 0) {
                val data = workDataOf(
                    "title" to title,
                    "message" to "Događaj je danas!"
                )

                val workRequest = OneTimeWorkRequestBuilder<NotificationWorker>()
                    .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                    .setInputData(data)
                    .build()

                WorkManager.getInstance(context).enqueue(workRequest)
            }
        }
}


