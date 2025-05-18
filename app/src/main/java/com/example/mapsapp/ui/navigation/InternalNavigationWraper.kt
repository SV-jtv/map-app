package com.example.mapsapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.example.mapsapp.ui.navigation.Destination.CreateMarker
import com.example.mapsapp.ui.navigation.Destination.DetailMarker
import com.example.mapsapp.ui.navigation.Destination.Map
import com.example.mapsapp.ui.navigation.Destination.List
import com.example.mapsapp.ui.screens.CreateMarkerScreen
import com.example.mapsapp.ui.screens.DetailMarkerScreen
import com.example.mapsapp.ui.screens.MapScreen
import com.example.mapsapp.ui.screens.MarkerListScreen

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun InternalNavigationWrapper(navController: NavHostController, modifier: Modifier) {
    //val navControler = rememberNavController()
    NavHost(navController, Map) {
        composable<Map>{
            MapScreen(modifier){
                    coordenades -> navController.navigate(CreateMarker(coordenades))
            }
        }
        composable<List> {
            MarkerListScreen(modifier){
                studentId -> navController.navigate(DetailMarker(studentId))
            }
        }
        composable<CreateMarker> { backStackEntry ->
            val pantallaCreate = backStackEntry.toRoute<CreateMarker>()
            CreateMarkerScreen(modifier, pantallaCreate.coordenades ){
                navController.popBackStack()
            }

        }
        composable<DetailMarker> { backStackEntry ->
            val pantallaDetail = backStackEntry.toRoute<DetailMarker>()
            DetailMarkerScreen(modifier, pantallaDetail.myParameter){
                navController.popBackStack()
            }
        }

    }
}

