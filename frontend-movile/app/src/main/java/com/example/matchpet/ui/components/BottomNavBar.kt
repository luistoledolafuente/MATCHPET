package com.example.matchpet.ui.components

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.VolunteerActivism
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

// 🔑 ¡CORRECCIÓN/AÑADIR ESTAS IMPORTACIONES DE TU PAQUETE DE TEMA!
// Asegúrate de que las rutas sean correctas para tu proyecto
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.ui.theme.SurfaceWhite


/**
 * Clase de datos para definir un ítem de la barra de navegación.
 */
data class NavItem(
    val route: String,
    val icon: ImageVector,
    val label: String
)

/**
 * Genera la Barra de Navegación Inferior (Bottom Navigation Bar) para móvil.
 *
 * @param navController El controlador de navegación para manejar las rutas.
 * @param navItems La lista de NavItem a mostrar (diferente para Adoptante y Refugio).
 */
@Composable
fun BottomNavBar(
    navController: NavController,
    navItems: List<NavItem>
) {
    // Obtiene la entrada actual del Back Stack para saber en qué ruta estamos
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar(
        // Utiliza el color blanco de tu tema como fondo
        containerColor = SurfaceWhite,
        tonalElevation = 4.dp,
        modifier = Modifier
    ) {
        navItems.forEach { item ->
            AddItem(
                item = item,
                currentRoute = currentRoute,
                navController = navController
            )
        }
    }
}

/**
 * Define la apariencia de cada ítem dentro de la barra de navegación.
 */
@Composable
fun RowScope.AddItem(
    item: NavItem,
    currentRoute: String?,
    navController: NavController
) {
    val isSelected = currentRoute == item.route

    NavigationBarItem(
        label = {
            Text(
                text = item.label,
                style = MaterialTheme.typography.labelSmall
            )
        },
        icon = {
            Icon(
                imageVector = item.icon,
                contentDescription = item.label,
            )
        },
        selected = isSelected,
        onClick = {
            // Navegación: Evita recomponer la pantalla si ya estamos en ella
            if (currentRoute != item.route) {
                navController.navigate(item.route) {
                    // Evita construir una pila grande de destinos en el back stack
                    // Esto navega de forma segura en el NavHost interno
                    popUpTo(navController.graph.startDestinationId) {
                        saveState = true
                    }
                    // Evita múltiples copias del mismo destino
                    launchSingleTop = true
                    // Restaura el estado al cambiar de pestaña
                    restoreState = true
                }
            }
        },
        // Personalización de colores para que coincida con tu estilo (PrimaryTeal)
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = PrimaryTeal,
            selectedTextColor = PrimaryTeal,
            indicatorColor = PrimaryTeal.copy(alpha = 0.1f), // Indicador ligero
            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
}


val adoptanteNavItems = listOf(
    // Rutas para Adoptante
    NavItem(route = "adoptante_home", icon = Icons.Default.Home, label = "Inicio"),
    NavItem(route = "adoptante_mascotas", icon = Icons.Default.Pets, label = "Mascotas"),
    NavItem(route = "adoptante_favoritos", icon = Icons.Default.Favorite, label = "Favoritos"),
    NavItem(route = "adoptante_donaciones", icon = Icons.Default.VolunteerActivism, label = "Donaciones"),
    NavItem(route = "adoptante_solicitudes", icon = Icons.Default.List, label = "Solicitudes"),
)

val refugioNavItems = listOf(
    // Rutas para Refugio
    NavItem(route = "refugio_home", icon = Icons.Default.Home, label = "Inicio"),
    NavItem(route = "refugio_mis_mascotas", icon = Icons.Default.Pets, label = "Mis Mascotas"),
    NavItem(route = "refugio_solicitudes", icon = Icons.Default.List, label = "Solicitudes"),
    NavItem(route = "refugio_donaciones", icon = Icons.Default.VolunteerActivism, label = "Donaciones"),
    NavItem(route = "refugio_historial", icon = Icons.Default.List, label = "Historial"),
)