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
import com.example.matchpet.data.repository.AnimalRepository // Importación necesaria
import com.example.matchpet.viewmodel.refugio.RefugioViewModel // Importación necesaria
import com.example.matchpet.ui.screens.refugio.MisMascotasScreen
import com.example.matchpet.ui.screens.refugio.NuevaMascotaScreen
import com.example.matchpet.ui.screens.refugio.RefugioHomeScreen
import com.example.matchpet.ui.screens.refugio.RefugioProfileScreen
import com.example.matchpet.ui.screens.refugio.SolicitudesRecibidasScreen
import com.example.matchpet.ui.screens.refugio.RefugioDonacionesScreen

@Composable
fun RefugioDashboardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    token: String,
    paddingValues: PaddingValues,
    // 🔑 AÑADIDOS: Estos son los parámetros que te faltaban en la función
    animalRepository: AnimalRepository,
    refugioViewModel: RefugioViewModel
) {
    NavHost(
        navController = navController,
        startDestination = "refugio_home",
        modifier = modifier
    ) {
        // 1. HOME
        composable("refugio_home") {
            // ¡Ahora pasamos los cinco parámetros!
            RefugioHomeScreen(
                navController = navController,
                token = token,
                animalRepository = animalRepository, // <--- ¡Importante!
                refugioViewModel = refugioViewModel // <--- ¡Importante!
            )
        }

        // 2. MIS MASCOTAS
        composable("refugio_mis_mascotas") {
            MisMascotasScreen(
                token = token,
                onNavigateToNewAnimal = { navController.navigate("nueva_mascota") },
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
            val animalId = backStackEntry.arguments?.getString("animalId")
            NuevaMascotaScreen(
                onBack = { navController.popBackStack() },
                token = token,
                animalId = animalId
            )
        }

        // 3. SOLICITUDES
        composable("refugio_solicitudes") {
            SolicitudesRecibidasScreen(token = token)
        }

        // 4. DONACIONES
        composable("refugio_donaciones") {
            RefugioDonacionesScreen()
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