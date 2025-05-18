package com.example.mapsapp.ui.navigation

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.Destination.Drawer
import com.example.mapsapp.ui.navigation.Destination.Permisions
import com.example.mapsapp.ui.navigation.Destination.LogIn
import com.example.mapsapp.ui.navigation.Destination.Register
import com.example.mapsapp.ui.screens.DrawerScreen
import com.example.mapsapp.ui.screens.PermissionsScren
import com.example.mapsapp.ui.screens.RegisterScreen
import com.example.mapsapp.ui.screens.LoginScreen



@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@Composable
fun MainNavigationWrapper() {
    val navController = rememberNavController()
    NavHost(navController, Permisions) {
        composable<Permisions> {
            PermissionsScren(navController) {
                navController.navigate(LogIn)
            }
        }
        composable<Drawer> {
            DrawerScreen(){
                navController.navigate(LogIn)
            }
        }
        composable<LogIn> {
            LoginScreen(
                { navController.navigate(Register) },
                { navController.navigate(Drawer) }
            )
        }
        composable<Register> {
            RegisterScreen() {
                navController.navigate(LogIn)
            }
        }
    }
}
