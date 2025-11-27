package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.components.BottomNavBar
import com.example.matchpet.ui.components.refugioNavItems
import com.example.matchpet.ui.navigation.RefugioDashboardNavHost
import com.example.matchpet.ui.theme.BackgroundLight // Asumo que BackgroundLight está aquí

@Composable
fun RefugioDashboardScreen(
    mainNavController: NavController, // Controlador principal (de AppNavigation)
    token: String
) {
    // 🔑 1. Controlador de navegación INTERNO para las pestañas
    val dashboardNavController = rememberNavController()

    Scaffold(
        bottomBar = {
            // 2. Pasamos el controlador INTERNO y los ítems de Refugio
            BottomNavBar(navController = dashboardNavController, navItems = refugioNavItems)
        }
    ) { paddingValues ->
        // 3. Usamos el NavHost interno de Refugio para mostrar el contenido
        RefugioDashboardNavHost(
            navController = dashboardNavController,
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundLight),
            token = token,
            paddingValues = paddingValues
        )
    }
}