package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.matchpet.data.model.MascotaCardData
import com.example.matchpet.ui.theme.WebBlueLight
import com.example.matchpet.ui.theme.WebCream
import com.example.matchpet.ui.theme.WebSalmon
import com.example.matchpet.ui.theme.WebTeal

@Composable
fun AdoptanteHomeScreen(navController: NavController, token: String, paddingValues: PaddingValues) {
    // Datos de ejemplo para Mascotas
    val nuevasMascotas = listOf(
        MascotaCardData("Max", "Mestizo", "2 años", "Refugio San Roque"),
        MascotaCardData("Luna", "Labrador", "1 año", "Refugio Esperanza"),
        MascotaCardData("Rocky", "Pastor Alemán", "3 años", "Patitas Felices"),
        MascotaCardData("Bella", "Golden Retriever", "4 años", "Refugio Norte"),
    )

    // Datos de ejemplo para Refugios Destacados
    val refugiosDestacados = listOf(
        RefugioCardData("Refugio Esperanza", "Lima, Perú", 4),
        RefugioCardData("Patitas Felices", "Arequipa, Perú", 5),
        RefugioCardData("Casa Rescate", "Cusco, Perú", 3)
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WebCream), // Fondo Crema Web
        contentPadding = paddingValues,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // -------------------------
        // HEADER
        // -------------------------
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "¡Hola, Adoptante! 🐾",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = WebTeal
                    )
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocationOn,
                            contentDescription = "Ubicación",
                            tint = WebSalmon,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Tu Ubicación",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            }
        }

        // -------------------------
        // BUSCADOR
        // -------------------------
        item {
            Box(modifier = Modifier.padding(horizontal = 24.dp)) {
                OutlinedTextField(
                    value = "",
                    onValueChange = {},
                    placeholder = { Text("Buscar mascota por tipo, refugio o raza…", color = Color.Gray) },
                    leadingIcon = { Icon(Icons.Default.Search, null, tint = Color.Gray) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(WebBlueLight.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = WebTeal,
                        unfocusedBorderColor = Color.LightGray,
                        cursorColor = WebTeal
                    ),
                    singleLine = true
                )
            }
        }

        // -------------------------
        // BANNER "MATCH PERFECTO"
        // -------------------------
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(WebBlueLight)
                    .padding(24.dp)
            ) {
                Column {
                    Text(
                        text = "¡Tu Match Perfecto te Espera!",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = WebTeal
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Te presentamos las mascotas más compatibles contigo según tus preferencias.",
                        fontSize = 14.sp,
                        color = Color(0xFF4A5568)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { navController.navigate("adoptante_mascotas") },
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Ver Recomendaciones", color = Color.White)
                    }
                }
            }
        }

        // -------------------------
        // NUEVAS MASCOTAS CERCA DE TI
        // -------------------------
        item { SectionTitle("Nuevas Mascotas Cerca de Ti", Modifier.padding(horizontal = 24.dp)) }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(nuevasMascotas) {
                    MascotaCardWebStyle(it)
                }
            }
        }

        // -------------------------
        // REFUGIOS DESTACADOS
        // -------------------------
        item { SectionTitle("Refugios Destacados", Modifier.padding(horizontal = 24.dp)) }

        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(horizontal = 24.dp)
            ) {
                items(refugiosDestacados) {
                    RefugioCardWebStyle(it)
                }
            }
        }
        
        item { Spacer(modifier = Modifier.height(24.dp)) }
    }
}

// ------------------------------------------------------
// DATA CLASSES & COMPONENTS
// ------------------------------------------------------

data class RefugioCardData(val nombre: String, val ubicacion: String, val estrellas: Int)

@Composable
fun SectionTitle(text: String, modifier: Modifier = Modifier) {
    Text(
        text = text,
        fontSize = 22.sp,
        fontWeight = FontWeight.Bold,
        color = Color(0xFF1F2937), // Gray-800
        modifier = modifier
    )
}

@Composable
fun MascotaCardWebStyle(item: MascotaCardData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WebCream),
        modifier = Modifier
            .width(200.dp)
            .height(280.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WebTeal.copy(alpha = 0.2f))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Placeholder imagen
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(Color.LightGray)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = WebTeal)
            Text(item.refugio, fontSize = 12.sp, color = Color.Gray)
            
            Spacer(modifier = Modifier.weight(1f))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(item.edad, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = WebSalmon)
                // Icono corazón placeholder
                Text("♥", color = Color.Gray, fontSize = 20.sp)
            }
        }
    }
}

@Composable
fun RefugioCardWebStyle(item: RefugioCardData) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = WebCream),
        modifier = Modifier
            .width(220.dp)
            .height(140.dp),
        elevation = CardDefaults.cardElevation(4.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, WebSalmon.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(item.nombre, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = WebTeal)
            
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 4.dp)) {
                Icon(Icons.Default.LocationOn, contentDescription = null, tint = Color.Gray, modifier = Modifier.size(12.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(item.ubicacion, fontSize = 12.sp, color = Color.Gray)
            }

            Spacer(modifier = Modifier.weight(1f))

            // Estrellas
            Row {
                repeat(5) { index ->
                    Text(
                        text = "★",
                        color = if (index < item.estrellas) Color(0xFFEAB308) else Color.LightGray, // Yellow-500
                        fontSize = 14.sp
                    )
                }
            }
            
            Text("Ver perfil", fontSize = 12.sp, color = WebSalmon, modifier = Modifier.padding(top = 4.dp))
        }
    }
}