package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.components.BottomNavBar
import com.example.matchpet.ui.components.adoptanteNavItems
import com.example.matchpet.ui.navigation.AdoptanteDashboardNavHost
import com.example.matchpet.ui.theme.BackgroundLight

@Composable
fun DashboardScreen(
    mainNavController: NavController, // Controlador principal (de la AppNavigation)
    token: String
) {
    // 🔑 1. Controlador de navegación INTERNO para las pestañas
    val dashboardNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 2. Pasamos el controlador INTERNO al BottomNavBar
            BottomNavBar(navController = dashboardNavController, navItems = adoptanteNavItems)
        }
    ) { paddingValues ->
        // 3. Usamos el NavHost interno para mostrar el contenido
        AdoptanteDashboardNavHost(
            navController = dashboardNavController,
            // 4. Pasamos el token y el padding que necesita el NavHost interno
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            token = token,
            paddingValues = paddingValues
        )
    }
}