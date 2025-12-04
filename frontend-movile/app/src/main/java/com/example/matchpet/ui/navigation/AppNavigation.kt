package com.example.matchpet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel // 🔑 Importación necesaria para el ViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.data.repository.AnimalRepository // 🔑 Importación necesaria para AnimalRepository
import com.example.matchpet.viewmodel.refugio.RefugioViewModel // 🔑 Importación necesaria para RefugioViewModel
import com.example.matchpet.ui.screens.*
import com.example.matchpet.ui.screens.adoptante.DashboardScreen
import com.example.matchpet.ui.screens.adoptante.ProfileScreen
import com.example.matchpet.ui.screens.adoptante.DonacionScreen
import com.example.matchpet.ui.screens.auth.LoginScreen
import com.example.matchpet.ui.screens.auth.RegisterScreen
import com.example.matchpet.ui.screens.refugio.RefugioDashboardScreen
import com.example.matchpet.utils.Injection

@Composable
fun AppNavigation(
    navController: NavHostController = rememberNavController(),
    // 🔑 NUEVO PARÁMETRO: AnimalRepository debe ser pasado desde la actividad principal.
    animalRepository: AnimalRepository
) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        composable("splash") { SplashScreen(navController) }

        composable("welcome") {
            WelcomeScreen(
                onAdoptClick = { navController.navigate("login") },
                onDonateClick = { navController.navigate("register") },
                onShelterClick = { navController.navigate("register") }
            )
        }

        composable("login") {
            LoginScreen(
                navController = navController,
                onRegisterClick = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                navController = navController,
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Dashboard Adoptante
        composable("adoptante/dashboard/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            DashboardScreen(navController, token)
        }


        // Dashboard Refugio
        composable("refugio/dashboard/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""

            // 🔑 PASO 1: OBTENER LA FACTORY DESDE TU OBJETO INJECTION
            val factory = Injection.provideRefugioViewModelFactory()

            // 🔑 PASO 2: USAR LA FACTORY para crear el ViewModel
            val refugioViewModel: RefugioViewModel = viewModel(factory = factory)

            // Pasamos el controlador principal y las dependencias (Repositorio y ViewModel)
            RefugioDashboardScreen(
                mainNavController = navController,
                token = token,
                animalRepository = animalRepository,
                refugioViewModel = refugioViewModel
            )
        }

        // Rutas Estáticas Globales
        composable("about_us") {
            AboutUsScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }

        // Perfil de Adoptante (ruta global sin navbar)
        composable("adoptante_perfil/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ProfileScreen(
                token = token,
                onBack = { navController.popBackStack() }
            )
        }

        // Pantalla de Donación
        composable("donacion/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            DonacionScreen(
                navController = navController,
                token = token
            )
        }

        // Pantalla de Donación a un Refugio específico
        composable("donacion/{token}/{refugioId}/{refugioNombre}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            val refugioId = backStackEntry.arguments?.getString("refugioId")?.toIntOrNull()
            val refugioNombre = backStackEntry.arguments?.getString("refugioNombre")
            DonacionScreen(
                navController = navController,
                token = token,
                refugioId = refugioId,
                refugioNombre = refugioNombre
            )
        }
    }
}