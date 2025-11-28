package com.example.matchpet.ui.screens.refugio

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
import com.example.matchpet.viewmodel.ProfileViewModel // ✅ CORRECCIÓN: Usamos el nombre de clase correcto

@Composable
fun RefugioDrawerContent(
    dashboardNavController: NavController,
    mainNavController: NavController,
    closeDrawer: () -> Unit,
    token: String,
    // ✅ CORRECCIÓN: Usamos el nombre de clase correcto
    viewModel: ProfileViewModel = viewModel()
) {
    // 1. 🔑 Observamos el StateFlow simple para nombre y email
    val simpleUser by viewModel.simpleUser.collectAsState()

    // 2. 🔑 Dispara la carga del perfil al abrir/inicializar el Drawer
    LaunchedEffect(token) {
        // Aseguramos que la información se carga usando el token
        viewModel.loadUserProfile(token)
    }

    // 3. 🔑 Usamos los datos reales del StateFlow
    val userName = simpleUser?.name ?: "Cargando Refugio..."
    val userEmail = simpleUser?.email ?: "..."


    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

        // --- CABECERA (Perfil del Refugio) ---
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
                text = userName, // Nombre del refugio logueado
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = userEmail, // Email del refugio logueado
                fontSize = 14.sp,
                color = SurfaceWhite
            )
        }

        Divider(Modifier.padding(vertical = 8.dp))

        // --- 1. Opción: Ver Perfil (Navegación interna del Dashboard) ---
        NavigationDrawerItem(
            label = { Text("Ver Perfil") },
            selected = false,
            onClick = {
                closeDrawer()
                dashboardNavController.navigate("refugio_perfil") // Ruta interna del Dashboard
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
                mainNavController.navigate("login") {
                    popUpTo(mainNavController.graph.id) { inclusive = true }
                }
            },
            icon = { Icon(Icons.Default.Logout, contentDescription = null, tint = Color.Red) }
        )
    }
}