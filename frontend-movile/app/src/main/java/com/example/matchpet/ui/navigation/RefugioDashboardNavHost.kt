package com.example.matchpet.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchpet.ui.screens.refugio.ProfileScreen
import com.example.matchpet.ui.screens.refugio.RefugioHomeScreen // Tu pantalla de contenido principal

@Composable
fun RefugioDashboardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    token: String,
    paddingValues: PaddingValues
) {
    // Las rutas deben coincidir con refugioNavItems de BottomNavBar.kt
    NavHost(
        navController = navController,
        startDestination = "refugio_home",
        modifier = modifier
    ) {
        // 1. HOME
        composable("refugio_home") {
            // RefugioHomeScreen ahora necesita el NavController y paddingValues para funcionar correctamente
            RefugioHomeScreen(
                navController = navController,
                token = token,
                paddingValues = paddingValues
            )
        }

        // 2. MIS MASCOTAS
        composable("refugio_mis_mascotas") {
            Text("Pantalla: Mis Mascotas (Contenido)", modifier.fillMaxSize())
        }

        // 3. SOLICITUDES
        composable("refugio_solicitudes") {
            Text("Pantalla: Solicitudes de Adopción (Contenido)", modifier.fillMaxSize())
        }

        // 4. DONACIONES
        composable("refugio_donaciones") {
            Text("Pantalla: Donaciones (Contenido)", modifier.fillMaxSize())
        }



        // 6. PERFIL
        composable("refugio_perfil") {
            ProfileScreen(
                token = token,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}