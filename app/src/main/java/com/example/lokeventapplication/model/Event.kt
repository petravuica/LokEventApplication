package com.example.lokeventapplication.model

import com.google.firebase.firestore.GeoPoint

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "",
    val address: String = "",
    val location: GeoPoint = GeoPoint(0.0,0.0),
    val isInterested: Boolean = false
)


