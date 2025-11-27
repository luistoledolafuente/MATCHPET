package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.components.BottomNavBar
import com.example.matchpet.ui.components.refugioNavItems
import com.example.matchpet.ui.navigation.RefugioDashboardNavHost
import com.example.matchpet.ui.theme.BackgroundLight
import com.example.matchpet.ui.theme.PrimaryTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RefugioDashboardScreen(
    mainNavController: NavController,
    token: String
) {
    val dashboardNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    // 1. Obtener la ruta actual para control dinámico
    val navBackStackEntry by dashboardNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    // Definimos las rutas y condiciones
    val profileRoute = "refugio_perfil" // La ruta que viene del Drawer
    val bottomBarRoutes = refugioNavItems.map { it.route }

    // Bandera para la BottomNavBar: ¿Estamos en una ruta principal?
    val isMainRoute = currentRoute in bottomBarRoutes

    // Bandera para la Top Bar: ¿Necesitamos el botón de Volver (<-)?
    val shouldShowBackButton = currentRoute == profileRoute

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            RefugioDrawerContent(
                dashboardNavController = dashboardNavController,
                mainNavController = mainNavController,
                closeDrawer = { scope.launch { drawerState.close() } },
                token = token
            )
        }
    ) {
        Scaffold(
            // -------------------------------------------------------------
            // TOP BAR DINÁMICA: Determina si es Menú (☰) o Volver (<-)
            // -------------------------------------------------------------
            topBar = {
                TopAppBar(
                    title = {
                        // Título dinámico
                        val titleText = if (currentRoute == profileRoute) "Mi Perfil" else "MatchPet - Refugio"
                        Text(titleText, color = PrimaryTeal)
                    },
                    navigationIcon = {
                        if (shouldShowBackButton) {
                            // RUTA DE PERFIL: Muestra flecha de volver (<-)
                            IconButton(onClick = { dashboardNavController.popBackStack() }) {
                                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver", tint = PrimaryTeal)
                            }
                        } else {
                            // OTRAS RUTAS: Muestra icono de menú hamburguesa (☰)
                            IconButton(onClick = { scope.launch { drawerState.open() } }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menú", tint = PrimaryTeal)
                            }
                        }
                    }
                )
            },

            // -------------------------------------------------------------
            // BOTTOM BAR CONDICIONAL: Solo visible en rutas principales
            // -------------------------------------------------------------
            bottomBar = {
                if (isMainRoute) {
                    // SOLO se muestra en rutas de la barra inferior.
                    BottomNavBar(navController = dashboardNavController, navItems = refugioNavItems)
                }
            }
        ) { paddingValues ->
            RefugioDashboardNavHost(
                navController = dashboardNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .background(BackgroundLight),
                token = token,
                paddingValues = paddingValues
            )
        }
    }
}