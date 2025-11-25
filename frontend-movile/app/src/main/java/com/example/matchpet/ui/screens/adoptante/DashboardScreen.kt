package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.matchpet.data.model.MascotaCardData
import com.example.matchpet.data.model.SolicitudData

@Composable
fun DashboardScreen(navController: NavController, token: String) {

    val recomendaciones = listOf(
        MascotaCardData("Max", "Mestizo", "2 años", "Refugio San Roque"),
        MascotaCardData("Luna", "Labrador", "1 año", "Refugio Esperanza"),
        MascotaCardData("Rocky", "Pastor Alemán", "3 años", "Patitas Felices"),
        MascotaCardData("Bella", "Golden Retriever", "4 años", "Refugio Norte"),
    )

    val nuevasMascotas = listOf(
        MascotaCardData("Toby", "Bulldog", "5 años", "Hogar Animal"),
        MascotaCardData("Kira", "Siames", "1 año", "Refugio Esperanza"),
    )

    val solicitudes = listOf(
        SolicitudData("Max", "Aprobada", "20 Nov 2025"),
        SolicitudData("Coco", "Pendiente", "18 Nov 2025")
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9F1)),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {

        // -------------------------
        // HEADER
        // -------------------------
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                Column {
                    Text(
                        text = "¡Hola, Adoptante! 🐾",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF244B57)
                    )
                    Text(
                        text = "Estas mascotas podrían ser tu pareja ideal 💛",
                        fontSize = 13.sp,
                        color = Color(0xFF6F6F6F)
                    )
                }

                IconButton(
                    onClick = {
                        navController.navigate("profile/$token")
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(Color(0xFF89CFBD), CircleShape)
                ) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar perfil", tint = Color.White)
                }
            }
        }

        // BUSCADOR
        item {
            OutlinedTextField(
                value = "",
                onValueChange = {},
                placeholder = { Text("Buscar mascota por tipo, refugio o raza…") },
                leadingIcon = { Icon(Icons.Default.Search, null) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(55.dp),
                singleLine = true
            )
        }

        // RECOMENDADAS
        item { SectionTitle("Recomendadas Para Ti") }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(recomendaciones) {
                    MascotaCard(it)
                }
            }
        }

        // NUEVAS CERCA DE TI
        item { SectionTitle("Nuevas Mascotas Cerca de Ti") }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                items(nuevasMascotas) {
                    MascotaCard(it)
                }
            }
        }

        // SOLICITUDES
        item { SectionTitle("Tus Últimas Solicitudes") }

        items(solicitudes) {
            SolicitudCard(it)
        }
    }
}

// ------------------------------------------------------
// COMPONENTES
// ------------------------------------------------------

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 18.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF346D77)
    )
}

@Composable
fun MascotaCard(item: MascotaCardData) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFEDEBFF)),
        modifier = Modifier
            .width(180.dp)
            .height(250.dp),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {

        Column(modifier = Modifier.padding(12.dp)) {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .background(Color(0xFFCCCCCC), RoundedCornerShape(14.dp))
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("${item.raza}, ${item.edad}", fontSize = 12.sp, color = Color(0xFF4F4F4F))
            Text(item.refugio, fontSize = 11.sp, color = Color(0xFF777777))

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {},
                modifier = Modifier
                    .height(34.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF9BD8C0))
            ) {
                Text("Ver Perfil", fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun SolicitudCard(item: SolicitudData) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEFE2)),
        shape = RoundedCornerShape(14.dp),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text("Mascota: ${item.mascota}", fontWeight = FontWeight.Bold)
            Text("Estado: ${item.estado}", fontSize = 12.sp, color = Color(0xFF585858))
            Text("Fecha: ${item.fecha}", fontSize = 12.sp, color = Color(0xFF7B7B7B))
        }
    }
}
