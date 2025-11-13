package com.example.matchpet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.screens.LoginScreen
import com.example.matchpet.ui.screens.RegisterScreen
import com.example.matchpet.ui.screens.WelcomeScreen
import com.example.matchpet.ui.theme.MatchPetTheme
import com.example.matchpet.ui.screens.ProfileScreen
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
        Surface(
            color = MaterialTheme.colorScheme.background
        ) {
            val navController = rememberNavController()
            AppNavigation(navController)
        }
    }
}

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = "welcome"
    ) {
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
                onRegisterClick = { /* acción al registrarse */ },
                onLoginClick = { navController.navigate("login") }
            )
        }

        // Perfil con token dinámico
        composable("profile/{token}") { backStackEntry ->
            val token = backStackEntry.arguments?.getString("token") ?: ""
            ProfileScreen(token = token)
        }
    }

}

