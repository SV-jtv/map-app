package com.example.mapsapp.ui.screens

import android.location.Location
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MyViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType



@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MapScreen(modifier: Modifier = Modifier, navigateToDetail: (String) -> Unit) {
    Column(Modifier.fillMaxSize()) {
        val myViewModel = viewModel<MyViewModel>()
        val markerList by myViewModel.markerList.observeAsState(emptyList())
        val context = LocalContext.current
        val itb = LatLng(41.4534225, 2.1837151)
        val cameraPositionState = rememberCameraPositionState {
            position = CameraPosition.fromLatLngZoom(itb, 16f)
        }
        LaunchedEffect(Unit) {
            myViewModel.getAllMarkers()
        }

        // 👇 Usamos MapProperties para establecer el tipo de mapa
        val mapProperties = remember {
            MapProperties(mapType = MapType.HYBRID)
        }

        GoogleMap(
            modifier = modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            properties = mapProperties, // 👈 Aquí está el cambio
            onMapClick = {
                Log.d("MAP CLICKED", it.toString())
            },
            onMapLongClick = {
                navigateToDetail(it.toString())
                Log.d("MAP CLICKED LONG", it.toString())
            }) {
            Marker(
                state = MarkerState(position = itb),
                title = "ITB",
                snippet = "Marker at ITB"
            )

            markerList.forEach { marker ->

                val icon = myViewModel.markerIcons[marker.image]

                LaunchedEffect(marker.image) {
                    if (icon == null) {
                        myViewModel.loadMarkerIcon(marker.image!!, context)
                    }
                }

                if (icon != null) {
                    val coords = marker.coordenades
                        .removePrefix("lat/lng:")
                    Marker(
                        state = MarkerState(position = StringToLatLng(marker.coordenades)),
                        title = marker.name,
                        snippet = "Coordenades: $coords",
                        icon = icon
                    )
                }
            }

        }

    }
}

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun StringToLatLng(coordenades: String): LatLng {
    val location = Location("manual").apply {
        val coords = coordenades
            .removePrefix("lat/lng: (")
            .removeSuffix(")")
            .split(",")
        latitude = coords[0].toDouble()
        longitude = coords[1].toDouble()
    }
    return LatLng(location.latitude, location.longitude)
}
