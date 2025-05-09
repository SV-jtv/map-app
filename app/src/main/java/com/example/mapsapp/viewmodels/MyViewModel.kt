package com.example.mapsapp.viewmodels

import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.mapsapp.MyApp
import com.example.mapsapp.data.Marker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class MyViewModel: ViewModel() {
    val database = MyApp.database

    private val _markerList = MutableLiveData<List<Marker>>()
    val markerList = _markerList

    private var _selectedMarker: Marker? = null

    private val _markerName = MutableLiveData<String>()
    val markerName = _markerName

    private val _markerCoordenades = MutableLiveData<String>()
    val markerCoordenades = _markerCoordenades

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
            val marker = Marker(
                name= name,
                coordenades= coordenades,
                image= image.toString()
            )
            database.insertMarker(name, coordenades, imageName)
        }
    }


    fun updateMarker(id: String, name: String, coordenades: String, image: Bitmap?){
        val stream = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.PNG, 0, stream)
        val imageName = _selectedMarker?.image?.removePrefix("https://aobflzinjcljzqpxpcxs.supabase.co/storage/v1/object/public/images/")
        CoroutineScope(Dispatchers.IO).launch {
            database.updateMarker(id, name, coordenades.toDouble(), imageName.toString(), stream.toByteArray())
        }
    }


    fun deleteMarker(id: String){
        CoroutineScope(Dispatchers.IO).launch {
            database.deleteMarker(id)
            getAllMarkers()
        }
    }

    fun getMarker(id: String){
        if(_selectedMarker == null){
            CoroutineScope(Dispatchers.IO).launch {
                val student = database.getMarker(id)
                withContext(Dispatchers.Main) {
                    _selectedMarker = student
                    _markerName.value = student.name
                    _markerCoordenades.value = student.coordenades.toString()
                }
            }
        }
    }

    fun editMarkerName(name: String) {
        _markerName.value = name
    }

    fun editMarkerCoordenades(Coordenades: String) {
        _markerCoordenades.value = Coordenades
    }

}

