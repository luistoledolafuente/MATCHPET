package com.example.matchpet.ui.screens.adoptante

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchpet.data.model.UserProfileResponse
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.WebBlueLight
import com.example.matchpet.ui.theme.WebCream
import com.example.matchpet.ui.theme.WebSalmon
import com.example.matchpet.ui.theme.WebTeal
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    token: String,
    onBack: () -> Unit = {}
) {
    var profile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Campos editables
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }

    val scroll = rememberScrollState()
    val context = LocalContext.current

    // --- Llamada al backend ---
    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getProfile("Bearer $token")
            if (response.isSuccessful) {
                profile = response.body()
                profile?.let { user ->
                    nombre = user.nombre
                    apellidoPaterno = user.apellidoPaterno
                    apellidoMaterno = user.apellidoMaterno
                    telefono = user.telefono ?: ""
                    fechaNacimiento = user.fechaNacimiento
                    direccion = user.direccion ?: ""
                    ciudad = user.ciudad ?: ""
                    pais = user.pais ?: ""
                }
            } else {
                error = "Error ${response.code()}: ${response.message()}"
            }

        } catch (e: Exception) {
            error = e.message
        }
    }

    // --- UI ---
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFFDFF3FF), Color(0xFFFDE8E4)) // Gradiente Web
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp)
        ) {

            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = WebSalmon)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        "Mi Perfil",
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = WebTeal
                    )
                }

                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = WebTeal
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (error != null) {
                Text("Error: $error", color = Color.Red)
                return@Column
            }

            if (profile == null) {
                CircularProgressIndicator(color = WebTeal, modifier = Modifier.align(Alignment.CenterHorizontally))
                return@Column
            }

            // CARD PRINCIPAL
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f)),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {

                    // FOTO DE PERFIL
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        AsyncImage(
                            model = "https://ui-avatars.com/api/?name=${nombre}+${apellidoPaterno}&background=FDB2A0&color=fff",
                            contentDescription = "Foto",
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CAMPOS DEL FORMULARIO
                    ProfileInputField("Nombre", nombre, Icons.Default.Person) { nombre = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Apellido Paterno", apellidoPaterno, Icons.Default.Person) { apellidoPaterno = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Apellido Materno", apellidoMaterno, Icons.Default.Person) { apellidoMaterno = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Teléfono", telefono, Icons.Default.Phone) { telefono = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Fecha de Nacimiento", fechaNacimiento, Icons.Default.DateRange) { fechaNacimiento = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Dirección", direccion, Icons.Default.Home) { direccion = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Ciudad", ciudad, Icons.Default.LocationOn) { ciudad = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("País", pais, Icons.Default.Public) { pais = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Email", profile!!.email, Icons.Default.Email, readOnly = true) {}

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTONES DE ACCIÓN
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Button(
                            onClick = {
                                if (profile == null) return@Button
                                
                                val request = com.example.matchpet.data.model.UpdateAdoptanteRequest(
                                    nombre = nombre,
                                    apellidoPaterno = apellidoPaterno,
                                    apellidoMaterno = apellidoMaterno,
                                    telefono = telefono,
                                    fechaNacimiento = fechaNacimiento,
                                    direccion = direccion,
                                    ciudad = ciudad,
                                    pais = pais
                                )

                                val scope = kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main)
                                scope.launch {
                                    try {
                                        val response = RetrofitClient.api.updateAdoptanteProfile(
                                            token = "Bearer $token",
                                            id = profile!!.usuarioId,
                                            request = request
                                        )
                                        if (response.isSuccessful) {
                                            Toast.makeText(context, "Perfil actualizado correctamente", Toast.LENGTH_SHORT).show()
                                            onBack()
                                        } else {
                                            Toast.makeText(context, "Error al actualizar: ${response.message()}", Toast.LENGTH_SHORT).show()
                                        }
                                    } catch (e: Exception) {
                                        Toast.makeText(context, "Error de conexión: ${e.message}", Toast.LENGTH_SHORT).show()
                                    }
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Guardar")
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // SECCIÓN CAMBIAR CONTRASEÑA
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.8f)),
                elevation = CardDefaults.cardElevation(4.dp)
            ) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = WebSalmon)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cambiar contraseña", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = WebTeal)
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "Te enviaremos un enlace a tu correo para cambiar tu contraseña.",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Button(
                        onClick = {
                            Toast.makeText(context, "Enlace enviado al correo", Toast.LENGTH_SHORT).show()
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Enviar enlace")
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
fun ProfileInputField(
    label: String,
    value: String,
    icon: ImageVector,
    readOnly: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(label, fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4A5568))
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = { if (!readOnly) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = true,
            leadingIcon = { Icon(icon, contentDescription = null, tint = Color.Gray) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = WebSalmon,
                unfocusedBorderColor = Color.LightGray,
                focusedContainerColor = Color.White,
                unfocusedContainerColor = if (readOnly) Color(0xFFF7FAFC) else Color.White
            )
        )
    }
}
