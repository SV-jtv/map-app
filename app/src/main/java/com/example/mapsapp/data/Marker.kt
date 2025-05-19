package com.example.mapsapp.data

import kotlinx.serialization.Serializable

@Serializable
data class Marker(
    val id: Int? = 0,
    val name: String,
    val coordenades: String,
    val image: String? = null  // Nullable para que no se actualice si no se pasa
)
