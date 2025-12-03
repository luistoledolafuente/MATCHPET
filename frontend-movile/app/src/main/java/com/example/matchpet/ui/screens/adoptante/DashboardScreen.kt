package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.matchpet.ui.components.BottomNavBar
import com.example.matchpet.ui.components.adoptanteNavItems
import com.example.matchpet.ui.navigation.AdoptanteDashboardNavHost
import com.example.matchpet.ui.theme.BackgroundLight
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebTeal
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    mainNavController: NavController,
    token: String
) {
    val dashboardNavController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            AdoptanteDrawerContent(
                dashboardNavController = dashboardNavController,
                mainNavController = mainNavController,
                closeDrawer = { scope.launch { drawerState.close() } },
                token = token
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text("MATCHPET", color = Color.White, fontWeight = FontWeight.Bold) },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = WebTeal),
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = Color.White)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController = dashboardNavController, navItems = adoptanteNavItems)
            }
        ) { paddingValues ->
            AdoptanteDashboardNavHost(
                navController = dashboardNavController,
                modifier = Modifier
                    .fillMaxSize()
                    .background(PaleTeal),
                token = token,
                paddingValues = paddingValues
            )
        }
    }
}