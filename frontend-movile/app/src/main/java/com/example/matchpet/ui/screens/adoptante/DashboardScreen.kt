package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.components.BottomNavBar
import com.example.matchpet.ui.components.adoptanteNavItems
import com.example.matchpet.ui.navigation.AdoptanteDashboardNavHost
import com.example.matchpet.ui.theme.BackgroundLight
import com.example.matchpet.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    mainNavController: NavController, // Controlador principal (de la AppNavigation)
    token: String
) {
    // Controlador de navegación INTERNO para las pestañas
    val dashboardNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdoptanteDrawerContent(
                dashboardNavController = dashboardNavController,
                mainNavController = mainNavController,
                closeDrawer = { scope.launch { drawerState.close() } },
                token = token
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MatchPet - Adoptante", color = PrimaryTeal) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = PrimaryTeal)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController = dashboardNavController, navItems = adoptanteNavItems)
            }
        ) { paddingValues ->
            AdoptanteDashboardNavHost(
                navController = dashboardNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight),
                token = token,
                paddingValues = paddingValues
            )
        }
    }
}