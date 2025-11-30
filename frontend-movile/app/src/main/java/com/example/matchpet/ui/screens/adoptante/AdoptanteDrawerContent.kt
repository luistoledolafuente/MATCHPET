package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.ui.theme.SurfaceWhite
import com.example.matchpet.viewmodel.adoptante.AdoptanteDashboardViewModel

@Composable
fun AdoptanteDrawerContent(
    dashboardNavController: NavController,
    mainNavController: NavController,
    closeDrawer: () -> Unit,
    token: String,
    viewModel: AdoptanteDashboardViewModel = viewModel()
) {
    // Observamos el StateFlow para nombre y email
    val simpleUser by viewModel.simpleUser.collectAsState()

    // Disparamos la carga del perfil al abrir/inicializar el Drawer
    LaunchedEffect(token) {
        viewModel.loadUserProfile(token)
    }

    // Usamos los datos reales del StateFlow
    val userName = simpleUser?.name ?: "Cargando..."
    val userEmail = simpleUser?.email ?: "..."

    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

        // --- CABECERA (Perfil del Adoptante) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(PrimaryTeal.copy(alpha = 0.9f))
                .padding(16.dp),
            horizontalAlignment = Alignment.Start
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Usuario",
                modifier = Modifier.size(56.dp),
                tint = Color.White
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = userName, // Nombre del adoptante logueado
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = userEmail, // Email del adoptante logueado
                fontSize = 14.sp,
                color = SurfaceWhite
            )
        }

        Divider(Modifier.padding(vertical = 8.dp))

        // --- 1. Opción: Ver Perfil (Navegación global sin navbar) ---
        NavigationDrawerItem(
            label = { Text("Ver Perfil") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("adoptante_perfil/$token") // Ruta global con token
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        // --- 2. Opción: Configuración (Navegación principal/global) ---
        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("settings") // Ruta global (AppNavigation)
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) }
        )

        // --- 3. Opción: Acerca de la App (Navegación principal/global) ---
        NavigationDrawerItem(
            label = { Text("Acerca de la App") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("about_us") // Ruta global (AppNavigation)
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )

        // --- 4. CERRAR SESIÓN ---
        Spacer(Modifier.weight(1f)) // Empuja el Logout hacia abajo
        Divider(Modifier.padding(vertical = 8.dp))

        NavigationDrawerItem(
            label = { Text("Cerrar Sesión", color = Color.Red) },
            selected = false,
            onClick = {
                closeDrawer()
                // Navegación principal a Login, limpiando el back stack
                mainNavController.navigate("login") {
                    popUpTo(mainNavController.graph.id) { inclusive = true }
                }
            },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red) }
        )
    }
}
