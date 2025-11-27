package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchpet.ui.theme.PrimaryTeal

// NOTA: @OptIn(ExperimentalMaterial3Api::class) y el Scaffold/TopAppBar han sido eliminados.
@Composable
fun ProfileScreen(token: String, onBack: () -> Unit) {

    // Datos estáticos de ejemplo
    val userName = "Refugio Patitas Felices"
    val userEmail = "contacto@patitasfelices.org"
    val userRole = "Refugio"

    // SOLO EL CONTENIDO (COLUMN)
    Column(
        modifier = Modifier
            .fillMaxSize()
            // Quitamos el paddingValues aquí, el padding ya se aplica desde el NavHost padre
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        // Icono de Perfil Grande
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(PrimaryTeal.copy(alpha = 0.2f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                Icons.Default.AccountCircle,
                contentDescription = "Foto de perfil",
                modifier = Modifier.size(90.dp),
                tint = PrimaryTeal
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Información del Usuario
        Text(
            text = userName,
            fontSize = 24.sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF244B57)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = userEmail,
            fontSize = 16.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Rol: $userRole",
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = PrimaryTeal
        )

        Divider(modifier = Modifier.padding(vertical = 24.dp), thickness = 1.dp, color = Color.LightGray)

        // Botón de Cerrar Sesión
        Button(
            onClick = { /* Lógica de cerrar sesión */ },
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFDC3545)),
            modifier = Modifier.fillMaxWidth(0.8f)
        ) {
            Text("Cerrar Sesión", fontSize = 16.sp)
        }
    }
}