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
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.viewmodel.adoptante.AdoptanteDashboardViewModel

@Composable
fun AdoptanteDrawerContent(
    dashboardNavController: NavController,
    mainNavController: NavController,
    closeDrawer: () -> Unit,
    token: String,
    viewModel: AdoptanteDashboardViewModel = viewModel()
) {
    val simpleUser by viewModel.simpleUser.collectAsState()

    LaunchedEffect(token) {
        viewModel.loadUserProfile(token)
    }

    val userName = simpleUser?.name ?: "Cargando..."
    val userEmail = simpleUser?.email ?: "..."

    ModalDrawerSheet(modifier = Modifier.width(300.dp)) {

        // --- CABECERA (Perfil del Adoptante) ---
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(WebTeal.copy(alpha = 0.9f))
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
                text = userName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = userEmail,
                fontSize = 14.sp,
                color = Color.White.copy(alpha = 0.9f)
            )
        }

        Divider(Modifier.padding(vertical = 8.dp))

        // --- 1. Opción: Ver Perfil ---
        NavigationDrawerItem(
            label = { Text("Ver Perfil") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("adoptante_perfil/$token")
            },
            icon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        // --- 2. Opción: Configuración ---
        NavigationDrawerItem(
            label = { Text("Configuración") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("settings")
            },
            icon = { Icon(Icons.Default.Settings, contentDescription = null) }
        )

        // --- 3. Opción: Acerca de la App ---
        NavigationDrawerItem(
            label = { Text("Acerca de la App") },
            selected = false,
            onClick = {
                closeDrawer()
                mainNavController.navigate("about_us")
            },
            icon = { Icon(Icons.Default.Info, contentDescription = null) }
        )

        // --- 4. CERRAR SESIÓN ---
        Spacer(Modifier.weight(1f))
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
