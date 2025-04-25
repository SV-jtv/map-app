package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.mapsapp.ui.navigation.Destination.CreateMarker
import com.example.mapsapp.ui.navigation.Destination.DetailMarker
import com.example.mapsapp.ui.navigation.Destination.Map
import com.example.mapsapp.ui.navigation.Destination.List
import com.example.mapsapp.ui.screens.CreateMarkerScreen
import com.example.mapsapp.ui.screens.DetailMarkerScreen
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen

@Composable
fun InternalNavigationWrapper(navController: NavHostController, modifier: Modifier) {
    val navControler = rememberNavController()
    NavHost(navControler, Map) {
        composable<Map>{
            MapScreen(modifier)
        }
        composable<List> {
            MarkerListScreen(modifier)
        }
        composable<CreateMarker> { backStackEntry ->
            val pantallaCreate = backStackEntry.toRoute<CreateMarker>()
            CreateMarkerScreen(pantallaCreate.coordenades)
            navController.popBackStack()

        }
        composable<DetailMarker> { backStackEntry ->
            val pantallaDetail = backStackEntry.toRoute<DetailMarker>()
            DetailMarkerScreen(pantallaDetail.myParameter)
            navController.popBackStack()
        }

    }
}

