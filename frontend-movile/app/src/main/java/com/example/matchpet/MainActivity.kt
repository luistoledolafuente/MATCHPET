package com.example.matchpet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.screens.LoginScreen
import com.example.matchpet.ui.screens.RegisterScreen
import com.example.matchpet.ui.screens.WelcomeScreen
import com.example.matchpet.ui.theme.MatchPetTheme
import com.example.matchpet.ui.screens.ProfileScreen
import com.example.matchpet.ui.screens.SplashScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MatchPetApp()
        }
    }
}

@Composable
fun MatchPetApp() {
    MatchPetTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            val navController = rememberNavController()
            AppNavigation(navController)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "splash"
    ) {
        // Pantalla de carga inicial
        composable("splash") {
            SplashScreen(navController = navController)
        }

        // Pantalla de bienvenida
        composable("welcome") {
            WelcomeScreen(
                onAdoptClick = { navController.navigate("login") },
                onDonateClick = { navController.navigate("register") },
                onShelterClick = { navController.navigate("register") }
            )
        }

        // Pantalla de login
        composable("login") {
            LoginScreen(
                navController = navController,
                onRegisterClick = { navController.navigate("register") }
            )
        }

        // Pantalla de registro (actualizada)
        composable("register") {
            RegisterScreen(
                navController = navController,
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Pantalla de perfil (recibe el token del registro o login)
        composable("profile/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ProfileScreen(token = token)
        }
    }
}
