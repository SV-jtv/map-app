package com.example.mapsapp.viewmodels

import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateMapOf
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import com.google.android.gms.maps.model.BitmapDescriptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyViewModel : ViewModel() {
    val database = MyApp.database

    private val _markerList = MutableLiveData<List<Marker>>()
    val markerList = _markerList

    private var _selectedMarker: Marker? = null

    private val _markerName = MutableLiveData<String>()
    val markerName = _markerName

    private val _markerCoordenades = MutableLiveData<String>()
    val markerCoordenades = _markerCoordenades

    private val _markerImageUrl = MutableLiveData<String?>()
    val markerImageUrl = _markerImageUrl

    private val _markerIcons = mutableStateMapOf<String, BitmapDescriptor>()
    val markerIcons: Map<String, BitmapDescriptor> = _markerIcons

    fun getAllMarkers() {
        CoroutineScope(Dispatchers.IO).launch {
            val databaseStudents = database.getAllMarkers()
            withContext(Dispatchers.Main) {
                _markerList.value = databaseStudents
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun insertNewMarker(name: String, coordenades: String, image: Bitmap?) {
        val stream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 0, stream)
        CoroutineScope(Dispatchers.IO).launch {
            val imageName = database.uploadImage(stream.toByteArray())
            val newMarker = Marker(
                name = name,
                coordenades = coordenades,
                image = imageName
            )
            database.insertMarker(newMarker)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun updateMarker(id: String, name: String, coordenades: String, image: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            val imageName = _selectedMarker?.image?.removePrefix("https://aobflzinjcljzqpxpcxs.supabase.co/storage/v1/object/public/images/")

            val imageBytes = image?.let {
                ByteArrayOutputStream().apply {
                    it.compress(Bitmap.CompressFormat.PNG, 100, this)
                }.toByteArray()} ?: ByteArray(0)  // Empty array if image == null

            database.updateMarker(id, name, coordenades, imageName ?: "", imageBytes)
        }
        /*deleteMarker(id)
        insertNewMarker(name, coordenades, image)*/
    }


    fun deleteMarker(id: String) {
        CoroutineScope(Dispatchers.IO).launch {
            database.deleteMarker(id)
            getAllMarkers()
        }
    }


    fun getMarker(id: String) {
        if (_selectedMarker == null) {
            CoroutineScope(Dispatchers.IO).launch {
                val student = database.getMarker(id)
                withContext(Dispatchers.Main) {
                    _selectedMarker = student
                    _markerName.value = student.name
                    _markerCoordenades.value = student.coordenades
                    _markerImageUrl.value = student.image
                }
            }
        }
    }

    fun loadMarkerIcon(url: String, context: Context) {
        if (_markerIcons.containsKey(url)) return // Evita recargas innecesarias

        viewModelScope.launch {
            val icon = database.getBitmapDescriptorFromUrl(context, url)

            if (icon != null) {
                _markerIcons[url] = icon
            }
        }
    }

    fun editMarkerName(name: String) {
        _markerName.value = name
    }

    fun setMarkerCoordenades(coordenades: String) {
        _markerCoordenades.value = coordenades
    }

    /*fun editMarkerImage(image: String) {
        _markerImage.value = image
    }*/

}

