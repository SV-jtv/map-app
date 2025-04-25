package com.example.mapsapp.ui.screens

import android.location.Location
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import com.google.android.gms.maps.model.LatLng



@Composable
fun CreateMarkerScreen(coordenades: String) {

}


@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StringToLatLng(coordenades: String): LatLng {
    /*val latlng: Array<String?> =
        coordenades.split(",".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    val latitude = latlng[0]!!.toDouble()
    val longitude = latlng[1]!!.toDouble()
    val lcation = LatLng(latitude, longitude)*/

    val latLng: Location = Location(coordenades)
    val location = LatLng(latLng.latitude, latLng.longitude)
    return location
}