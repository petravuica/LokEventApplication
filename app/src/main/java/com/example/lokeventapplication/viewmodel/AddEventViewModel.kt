package com.example.lokeventapplication.viewmodel


import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.lokeventapplication.model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.GeoPoint
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException
import com.example.lokeventapplication.model.EventUiState

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
    fun saveEvent(onSuccess: () -> Unit, onError: (String) -> Unit) {
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
                    .addOnSuccessListener { onSuccess() }
                    .addOnFailureListener { onError("Greška pri spremanju: ${it.message}") }
            }
        } catch (e: DateTimeParseException) {
            onError("Datum mora biti u formatu yyyy-MM-dd.")
        } catch (e: Exception) {
            onError("Greška: ${e.message}")
        }
    }

    fun toggleInterest(event: Event) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = db.collection("users")
            .document(userId)
            .collection("interests")
            .document(event.id)

        if (event.isInterested) {
            docRef.delete()
        } else {
            docRef.set(
                mapOf(
                    "eventId" to event.id,
                    "title" to event.title,
                    "description" to event.description,
                    "date" to event.date,
                    "category" to event.category,
                    "address" to event.address,
                    "location" to event.location
                )
            )
        }
    }
}


