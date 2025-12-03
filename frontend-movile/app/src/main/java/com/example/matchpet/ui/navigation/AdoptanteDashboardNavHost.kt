package com.example.matchpet.ui.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.matchpet.ui.screens.adoptante.AdoptanteHomeScreen
import com.example.matchpet.ui.screens.adoptante.AdoptanteMascotasScreen
import com.example.matchpet.ui.screens.adoptante.AdoptanteSolicitudesScreen
import com.example.matchpet.ui.screens.adoptante.AnimalDetailScreen
import com.example.matchpet.ui.screens.adoptante.FavoritesScreen

@Composable
fun AdoptanteDashboardNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    token: String,
    paddingValues: PaddingValues
) {
    NavHost(
        navController = navController,
        startDestination = "adoptante_home",
        modifier = modifier
    ) {
        // 1. Pestaña de Inicio
        composable("adoptante_home") {
            AdoptanteHomeScreen(
                navController = navController,
                token = token,
                paddingValues = paddingValues
            )
        }

        // 2. Pestaña de Favoritos
        composable("adoptante_favoritos") {
            FavoritesScreen(
                token = token,
                navController = navController
            )
        }

        // 3. Pestaña de Mascotas
        composable("adoptante_mascotas") {
            AdoptanteMascotasScreen(
                token = token,
                paddingValues = paddingValues,
                navController = navController
            )
        }

        // 4. Pestaña de Donaciones
        composable("adoptante_donaciones") {
            Text("Pantalla de Donaciones (Contenido)", modifier.fillMaxSize())
        }

        // 5. Pestaña de Solicitudes
        composable("adoptante_solicitudes") {
            AdoptanteSolicitudesScreen(
                token = token,
                paddingValues = paddingValues
            )
        }
        
        // 6. Detalles de Animal
        composable(
            route = "animal_detail/{animalId}",
            arguments = listOf(navArgument("animalId") { type = NavType.IntType })
        ) { backStackEntry ->
            val animalId = backStackEntry.arguments?.getInt("animalId") ?: 0
            AnimalDetailScreen(
                animalId = animalId,
                token = token,
                navController = navController
            )
        }
    }
}