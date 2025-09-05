package com.example.lokeventapplication.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import com.example.lokeventapplication.model.Event
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow


class EventsViewModel : ViewModel() {

    private val db = Firebase.firestore

    private val _events = MutableStateFlow<List<Event>>(emptyList())
    val events: StateFlow<List<Event>> = _events //cim se promjeni, odmah se updejta

    fun fetchEvents() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return //dohvati se usera

        db.collection("users").document(userId).collection("interests") //za svakog korisnika, kolekcija interests
            .get() //dohvaćeni svi eventi iz interests
            .addOnSuccessListener { interestDocs ->
                val interestedIds = interestDocs.map { it.id }.toSet() //stavljam ih u set

                db.collection("events")
                    .get()
                    .addOnSuccessListener { eventDocs -> //svi eventi iz baze
                        val eventList = eventDocs.mapNotNull { doc ->
                            try {
                                val event = doc.toObject(Event::class.java).copy( //firebase dokument u event model
                                    id = doc.id,
                                    isInterested = interestedIds.contains(doc.id)
                                )
                                Log.d("EventsDebug", "Fetched event: $event") // Ovdje ispis
                                event
                            } catch (e: Exception) {
                                Log.e("EventsDebug", "Parsing error: ${e.message}")
                                null
                            }
                        }
                        _events.value = eventList //sprema se u logove
                        Log.d("EventsDebug", "Total events fetched: ${eventList.size}")
                    }
                    .addOnFailureListener {
                        Log.e("EventsDebug", "Error fetching events", it)
                    }
            }
            .addOnFailureListener {
                Log.e("EventsDebug", "Error fetching interests", it)
            }
    }

    fun deleteEvent(eventId: String, onSuccess: () -> Unit, onError: (String) -> Unit) {
        db.collection("events").document(eventId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.message ?: "Greška pri brisanju") }
    }

    fun toggleInterest(event: Event) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val docRef = db.collection("users")
            .document(userId)
            .collection("interests") //za svakog korisnika, kolekcija
            .document(event.id) // stvara se dokument = event.idu

        if (event.isInterested) {
            docRef.delete()
        } else {
            val eventMap = mapOf(
                "title" to event.title,
                "description" to event.description,
                "date" to event.date,
                "location" to event.location
            )
            docRef.set(eventMap) //spremimo ga u kolekciju interests
        }

        fetchEvents()
    }
}