package com.example.matchpet.ui.screens.adoptante

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchpet.data.model.Animal
import com.example.matchpet.data.model.SolicitudRequest
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.WebBlueLight
import com.example.matchpet.ui.theme.WebCream
import com.example.matchpet.ui.theme.WebSalmon
import com.example.matchpet.ui.theme.WebTeal
import kotlinx.coroutines.launch

@Composable
fun AdoptanteMascotasScreen(
    token: String,
    paddingValues: PaddingValues
) {
    var mascotas by remember { mutableStateOf<List<Animal>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    
    // Estados para el Modal de Solicitud
    var showDialog by remember { mutableStateOf(false) }
    var selectedAnimal by remember { mutableStateOf<Animal?>(null) }
    var mensajeAdoptante by remember { mutableStateOf("") }
    var submitting by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    // Cargar Mascotas
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getAnimales()
            if (response.isSuccessful) {
                mascotas = response.body()?.content ?: emptyList()
            } else {
                error = "Error al cargar mascotas: ${response.message()}"
            }
        } catch (e: Exception) {
            error = "Error de conexión: ${e.message}"
        } finally {
            loading = false
        }
    }

    // UI Principal
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WebCream)
            .padding(paddingValues)
    ) {
        if (loading) {
            CircularProgressIndicator(
                modifier = Modifier.align(Alignment.Center),
                color = WebTeal
            )
        } else if (error != null) {
            Text(
                text = error!!,
                color = Color.Red,
                modifier = Modifier.align(Alignment.Center)
            )
        } else {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Explorar Mascotas 🐾",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = WebTeal,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                if (mascotas.isEmpty()) {
                    Text("No hay mascotas disponibles por ahora.", color = Color.Gray)
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(mascotas) { animal ->
                            MascotaGridItem(
                                animal = animal,
                                onSolicitarClick = {
                                    selectedAnimal = animal
                                    showDialog = true
                                }
                            )
                        }
                    }
                }
            }
        }

        // Modal de Solicitud
        if (showDialog && selectedAnimal != null) {
            AlertDialog(
                onDismissRequest = { showDialog = false },
                title = { Text("Adoptar a ${selectedAnimal!!.nombre}", color = WebTeal) },
                text = {
                    Column {
                        Text("Escribe un mensaje al refugio explicando por qué eres el adoptante ideal:")
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = mensajeAdoptante,
                            onValueChange = { mensajeAdoptante = it },
                            modifier = Modifier.fillMaxWidth(),
                            minLines = 3,
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = WebTeal,
                                cursorColor = WebTeal
                            )
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (mensajeAdoptante.isBlank()) {
                                Toast.makeText(context, "El mensaje es obligatorio", Toast.LENGTH_SHORT).show()
                                return@Button
                            }
                            
                            scope.launch {
                                submitting = true
                                try {
                                    val request = SolicitudRequest(
                                        animalId = selectedAnimal!!.animal_id,
                                        mensajeAdoptante = mensajeAdoptante
                                    )
                                    val response = RetrofitClient.api.createSolicitud("Bearer $token", request)
                                    if (response.isSuccessful) {
                                        Toast.makeText(context, "Solicitud enviada con éxito", Toast.LENGTH_LONG).show()
                                        showDialog = false
                                        mensajeAdoptante = ""
                                    } else {
                                        Toast.makeText(context, "Error al enviar: ${response.message()}", Toast.LENGTH_SHORT).show()
                                    }
                                } catch (e: Exception) {
                                    Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                                } finally {
                                    submitting = false
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        enabled = !submitting
                    ) {
                        if (submitting) {
                            CircularProgressIndicator(color = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("Enviar Solicitud")
                        }
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showDialog = false }) {
                        Text("Cancelar", color = Color.Gray)
                    }
                },
                containerColor = Color.White
            )
        }
    }
}

@Composable
fun MascotaGridItem(
    animal: Animal,
    onSolicitarClick: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Imagen
            AsyncImage(
                model = if (!animal.fotos.isNullOrEmpty()) "http://10.0.2.2:8081${animal.fotos[0]}" else "https://placehold.co/400x300/a8d8e0/316B7A?text=No+Photo",
                contentDescription = animal.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(140.dp)
                    .background(Color.LightGray),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Text(
                    text = animal.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp,
                    color = WebTeal
                )
                Text(
                    text = "${animal.raza ?: "Mestizo"} • ${animal.genero ?: "?"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = animal.refugioNombre ?: "Refugio",
                    fontSize = 12.sp,
                    color = WebSalmon,
                    fontWeight = FontWeight.SemiBold
                )
                
                Spacer(modifier = Modifier.height(12.dp))
                
                Button(
                    onClick = onSolicitarClick,
                    modifier = Modifier.fillMaxWidth().height(36.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                    shape = RoundedCornerShape(8.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text("Adoptar", fontSize = 12.sp)
                }
            }
        }
    }
}
