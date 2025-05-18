package com.example.mapsapp.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.example.mapsapp.ui.navigation.DrawerItem
import com.example.mapsapp.ui.navigation.InternalNavigationWrapper
import com.example.mapsapp.utils.SharedPreferencesHelper
import com.example.mapsapp.viewmodels.AuthViewModel
import com.example.mapsapp.viewmodels.AuthViewModelFactory
import kotlinx.coroutines.launch

@RequiresApi(Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DrawerScreen(onLogout: () -> Unit) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var selectedItemIndex by remember { mutableIntStateOf(0) }
    val context = LocalContext.current
    val authviewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(SharedPreferencesHelper(context))
    )
    ModalNavigationDrawer(
        gesturesEnabled = false,
        drawerContent = {
            ModalDrawerSheet {
                DrawerItem.entries.forEachIndexed { index, drawerItem ->
                    NavigationDrawerItem(
                        icon = {
                            Icon(
                                imageVector = drawerItem.icon,
                                contentDescription = drawerItem.text
                            )
                        },
                        label = { Text(text = drawerItem.text) },
                        selected = index == selectedItemIndex,
                        onClick = {
                            selectedItemIndex = index
                            scope.launch { drawerState.close() }
                            navController.navigate(drawerItem.route)
                        }
                    )
                }
            }
        },
        drawerState = drawerState
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("Maps App") }, //Maps App
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(imageVector = Icons.Default.Menu, contentDescription = "Menu")
                        }
                    },
                    actions = {
                        Row(
                            modifier = Modifier.fillMaxHeight().padding(end = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(onClick = {
                                authviewModel.logout()
                                onLogout()
                            }) {
                                Icon(
                                    imageVector = Icons.Default.AccountCircle,
                                    contentDescription = "User"
                                )
                            }
                        }
                    }
                )
            }
        ) { innerPadding ->
            InternalNavigationWrapper(navController, Modifier.padding(innerPadding))
        }

    }

}