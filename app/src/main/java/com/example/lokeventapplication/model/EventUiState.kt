package com.example.lokeventapplication.model

data class EventUiState(
    val title: String = "",
    val description: String = "",
    val date: String = "",
    val category: String = "",
    val address: String = "",
    val latitude: String = "",
    val longitude: String = ""
)
//UI koristi ovo stanje i ažurira ga kroz onValueChange() kad korisnik nešto upiše u formu.