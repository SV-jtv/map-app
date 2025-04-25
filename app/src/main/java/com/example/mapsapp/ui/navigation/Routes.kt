package com.example.mapsapp.ui.navigation

import com.google.android.gms.maps.model.LatLng
import kotlinx.serialization.Serializable


sealed class Destination {
    @Serializable
    object Permisions: Destination()

    @Serializable
    object Drawer: Destination()

    @Serializable
    object Map: Destination()

    @Serializable
    object List: Destination()

    @Serializable
    data class CreateMarker(val coordenades: String)

    @Serializable
    data class DetailMarker(val myParameter: String)
}
