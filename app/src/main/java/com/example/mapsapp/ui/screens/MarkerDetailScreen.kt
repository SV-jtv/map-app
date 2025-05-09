package com.example.mapsapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mapsapp.viewmodels.MyViewModel
import androidx.compose.runtime.getValue


@Composable
fun DetailMarkerScreen(modifier: Modifier, studentId: String, navigateBack: () -> Unit) {
    val myViewModel = viewModel<MyViewModel>()
    myViewModel.getMarker(studentId)
    val studentName: String by myViewModel.markerName.observeAsState("")
    val studentMark: String by myViewModel.markerCoordenades.observeAsState("")
    Column(modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        TextField(value = studentName, onValueChange = { myViewModel.editMarkerName(it) })
        TextField(value = studentMark, onValueChange = { myViewModel.editMarkerCoordenades(it) })
        Button(onClick = {
            //myViewModel.updateMarker(studentId, studentName, studentMark,  )
            navigateBack()}) {
            Text("Update")
        }
    }

}