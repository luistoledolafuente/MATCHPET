package com.example.matchpet

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.navigation.AppNavigation
import com.example.matchpet.ui.theme.MatchPetTheme
import com.example.matchpet.utils.Injection // 🔑 Importación necesaria

class MainActivity : ComponentActivity() {
    private lateinit var animalRepository: com.example.matchpet.data.repository.AnimalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Injection.initialize(applicationContext)
        animalRepository = Injection.animalRepository

        setContent {
            MatchPetTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()
                    AppNavigation(
                        navController = navController,
                        animalRepository = animalRepository
                    )
                }
            }
        }
    }
    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        intent?.data?.let { uri ->
            val token = uri.getQueryParameter("token")
            if (token != null) {
                Toast.makeText(this, "Token recibido: $token", Toast.LENGTH_LONG).show()
                // Aquí envías el token a tu AuthViewModel
            }
        }
    }


}