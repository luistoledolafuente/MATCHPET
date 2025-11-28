package com.example.matchpet.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.matchpet.ui.screens.*
import com.example.matchpet.ui.screens.adoptante.DashboardScreen
import com.example.matchpet.ui.screens.adoptante.ProfileScreen // Se mantiene la importación si la usas en otro sitio
import com.example.matchpet.ui.screens.auth.LoginScreen
import com.example.matchpet.ui.screens.auth.RegisterScreen
import com.example.matchpet.ui.screens.refugio.RefugioDashboardScreen

@Composable
fun AppNavigation(navController: NavHostController) {
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
            // Pasa el controlador principal
            RefugioDashboardScreen(mainNavController = navController, token = token)
        }

        // Rutas Estáticas Globales
        composable("about_us") {
            AboutUsScreen(navController = navController)
        }

        composable("settings") {
            SettingsScreen(navController = navController)
        }


    }
}