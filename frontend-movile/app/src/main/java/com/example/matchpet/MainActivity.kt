package com.example.matchpet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.navigation.AppNavigation
import com.example.matchpet.ui.theme.MatchPetTheme
import com.example.matchpet.utils.Injection // 🔑 Importación necesaria

class MainActivity : ComponentActivity() {

    // 🔑 OBTENEMOS EL REPOSITORIO USANDO TU OBJETO DE INYECCIÓN
    // Lo inicializaremos en onCreate después de la llamada a initialize.
    private lateinit var animalRepository: com.example.matchpet.data.repository.AnimalRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 🔑 1. INICIALIZAR EL OBJETO INJECTION CON EL CONTEXTO
        // Esto es crucial porque tu objeto Injection necesita el context.
        Injection.initialize(applicationContext)

        // 🔑 2. OBTENER LA INSTANCIA DEL REPOSITORIO
        // Ahora que Injection está inicializado, podemos acceder a animalRepository.
        animalRepository = Injection.animalRepository

        setContent {
            MatchPetTheme {
                Surface(color = MaterialTheme.colorScheme.background) {
                    val navController = rememberNavController()

                    // 🔑 CORRECCIÓN FINAL: Pasamos la instancia obtenida.
                    AppNavigation(
                        navController = navController,
                        animalRepository = animalRepository
                    )
                }
            }
        }
    }
}