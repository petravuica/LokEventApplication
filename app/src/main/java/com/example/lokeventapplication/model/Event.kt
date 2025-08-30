package com.example.lokeventapplication.model

import com.google.firebase.firestore.GeoPoint

data class Event(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val date: String = "", // format: yyyy-MM-dd
    val category: String = "",   // nova kategorija (npr. koncert, sport, edukacija...)
    val address: String = "",    // čitljiva adresa, za prikaz
    val location: GeoPoint = GeoPoint(0.0,0.0),
    val isInterested: Boolean = false
)


