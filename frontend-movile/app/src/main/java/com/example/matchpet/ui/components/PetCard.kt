package com.example.matchpet.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.ui.theme.WebTeal

@Composable
fun PetCard(
    animal: Animal,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onViewProfile: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onViewProfile)
    ) {
        Box {
            Column {
                // Imagen
                AsyncImage(
                    model = if (!animal.fotos.isNullOrEmpty()) "http://10.0.2.2:8081${animal.fotos[0]}" else "https://placehold.co/400x300/B2D8D8/004D40?text=No+Photo",
                    contentDescription = animal.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp) // Slightly taller as per design feel
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(12.dp)) {
                    // Nombre
                    Text(
                        text = animal.nombre,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = WebTeal,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    // Detalles (Raza • Genero)
                    Text(
                        text = "${animal.raza ?: "Mestizo"} • ${animal.genero ?: "?"}",
                        fontSize = 12.sp,
                        color = Color.Gray,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    // Refugio
                    Text(
                        text = animal.refugioNombre ?: "Refugio",
                        fontSize = 12.sp,
                        color = WebTeal,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    // Botón Ver Perfil
                    Button(
                        onClick = onViewProfile,
                        modifier = Modifier.fillMaxWidth().height(36.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Text("Ver Perfil", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            // Botón de Favorito (Superpuesto)
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
                    .background(Color.White.copy(alpha = 0.8f), CircleShape)
                    .size(32.dp)
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = "Favorito",
                    tint = if (isFavorite) Color.Red else Color.Gray,
                    modifier = Modifier.size(20.dp)
                )

            }

            // Badge Adoptado
            if (animal.estadoAdopcion.equals("Adoptado", ignoreCase = true)) {
                Surface(
                    color = Color.Red,
                    shape = RoundedCornerShape(bottomEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopStart)
                ) {
                    Text(
                        text = "ADOPTADO",
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
        }
    }
}
