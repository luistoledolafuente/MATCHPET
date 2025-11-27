package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
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
            topBar = {
                TopAppBar(
                    title = { Text("MatchPet - Refugio", color = PrimaryTeal) },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menú", tint = PrimaryTeal)
                        }
                    }
                )
            },
            bottomBar = {
                BottomNavBar(navController = dashboardNavController, navItems = refugioNavItems)
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