package com.example.matchpet.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchpet.ui.screens.adoptante.ProfileScreen
import com.example.matchpet.ui.screens.refugio.MisMascotasScreen
import com.example.matchpet.ui.screens.refugio.RefugioHomeScreen // Tu pantalla de contenido principal
import com.example.matchpet.ui.screens.refugio.RefugioProfileScreen

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
            MisMascotasScreen(
                token = token,
                // Función de navegación para el FAB (Floating Action Button) de 'Mis Mascotas'
                onNavigateToNewAnimal = { navController.navigate("nueva_mascota") }
            )
        }
        // 2b. AGREGAR NUEVA MASCOTA (Ruta de destino del FAB)
        composable("nueva_mascota") {
            // ✅ TODO: Implementar la pantalla real para agregar una mascota
            NuevaMascotaScreen(
                onBack = { navController.popBackStack() },
                token = token
            )
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
            RefugioProfileScreen(
                token = token,
                onBack = { navController.popBackStack() }
            )
        }

    }
}

// 🔑 Placeholder necesario para que compile la ruta 'nueva_mascota'
@Composable
fun NuevaMascotaScreen(onBack: () -> Unit, token: String) {
    Text("Pantalla: Formulario para Nueva Mascota. Token: $token", )
    // Puedes llamar a onBack() si necesitas un botón de regreso
}