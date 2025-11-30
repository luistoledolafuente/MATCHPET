package com.example.matchpet.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.matchpet.ui.screens.refugio.MisMascotasScreen
import com.example.matchpet.ui.screens.refugio.NuevaMascotaScreen
import com.example.matchpet.ui.screens.refugio.RefugioHomeScreen // Tu pantalla de contenido principal
import com.example.matchpet.ui.screens.refugio.RefugioProfileScreen
import com.example.matchpet.ui.screens.refugio.SolicitudesRecibidasScreen

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
                onNavigateToNewAnimal = { navController.navigate("nueva_mascota") },
                // 🔑 NUEVO: Función para navegar a la pantalla de edición, pasando el ID.
                onNavigateToEditAnimal = { animalId ->
                    navController.navigate("nueva_mascota?animalId=$animalId")
                }
            )
        }

        // 2b. AGREGAR / EDITAR NUEVA MASCOTA (La pantalla de formulario)
        composable(
            route = "nueva_mascota?animalId={animalId}",
            arguments = listOf(
                navArgument("animalId") { defaultValue = "" }
            )
        ) { backStackEntry ->
            // El ID que viene por la URL es String?
            val animalId = backStackEntry.arguments?.getString("animalId")
            NuevaMascotaScreen(
                onBack = { navController.popBackStack() },
                token = token,
                // ✅ CORRECCIÓN: El parámetro en la función es 'animalId', no 'animalIdToEdit'
                animalId = animalId
            )
        }

        // 3. SOLICITUDES
        composable("refugio_solicitudes") {
            // 🔑 REEMPLAZO DEL PLACEHOLDER por la pantalla real
            SolicitudesRecibidasScreen(token = token)
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
