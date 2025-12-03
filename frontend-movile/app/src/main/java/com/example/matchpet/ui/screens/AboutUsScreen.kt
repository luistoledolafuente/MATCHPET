package com.example.matchpet.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.matchpet.ui.theme.PrimaryTeal
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutUsScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Acerca de MatchPet", fontWeight = FontWeight.Bold, color = PrimaryTeal) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver atrás")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text("Nuestra Misión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "MatchPet nació con la misión de conectar refugios y adoptantes responsables, facilitando el proceso de adopción y garantizando un hogar amoroso para cada animal necesitado. Creemos en la tecnología como herramienta para construir un mundo con más finales felices.",
                style = MaterialTheme.typography.bodyLarge
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("El Equipo", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text(
                "Somos un pequeño equipo de amantes de los animales dedicados a mejorar la gestión de los refugios y hacer que la adopción sea el camino más fácil y transparente.",
                style = MaterialTheme.typography.bodyMedium
            )

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text("Contacto y Versión", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.SemiBold)
            Text("Correo Electrónico: info@matchpet.com", style = MaterialTheme.typography.bodyMedium)
            Text("Versión de la Aplicación: 1.0. Beta (Estable)", style = MaterialTheme.typography.bodySmall)
        }
    }
}