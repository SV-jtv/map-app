package com.example.mapsapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.Destination.Drawer
import com.example.mapsapp.ui.navigation.Destination.Permisions
import com.example.mapsapp.ui.screens.DrawerScreen
import com.example.mapsapp.ui.screens.PermissionsScren


@Composable
fun MainNavigationWrapper() {
    val navControler = rememberNavController()
    NavHost(navControler, Permisions) {
        composable<Permisions> {
            PermissionsScren(navControler) {
                navControler.navigate(Drawer)
            }
        }
        composable<Drawer> {
            DrawerScreen()
        }

    }
}
