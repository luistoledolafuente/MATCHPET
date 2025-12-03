package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchpet.data.model.refugio.RefugioUpdateRequest
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.data.repository.RefugioRepository
import com.example.matchpet.ui.theme.WebSalmon
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.viewmodel.refugio.RefugioViewModel
import com.example.matchpet.viewmodel.refugio.RefugioViewModelFactory

@Composable
fun RefugioProfileScreen(
    token: String,
    onBack: () -> Unit = {}
) {
    val viewModel: RefugioViewModel = viewModel(
        factory = RefugioViewModelFactory(
            RefugioRepository(RetrofitClient.api)
        )
    )

    val refugio by viewModel.refugio.collectAsState()
    val loading by viewModel.loading.collectAsState()
    val error by viewModel.errorMessage.collectAsState()
    val success by viewModel.successMessage.collectAsState()

    var editMode by remember { mutableStateOf(false) }

    var nombre by remember { mutableStateOf("") }
    var descripcion by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var personaContacto by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var urlSitioWeb by remember { mutableStateOf("") }

    val scroll = rememberScrollState()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.loadProfile(token)
    }

    LaunchedEffect(refugio) {
        refugio?.let {
            nombre = it.nombre
            descripcion = it.descripcion
            pais = it.pais
            ciudad = it.ciudad
            direccion = it.direccion
            email = it.email
            personaContacto = it.personaContacto ?: ""
            telefono = it.telefono ?: ""
            urlSitioWeb = it.urlSitioWeb ?: ""
        }
    }

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
                        "Perfil del Refugio",
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

            if (loading) {
                CircularProgressIndicator(color = WebTeal, modifier = Modifier.align(Alignment.CenterHorizontally))
            }

            error?.let {
                Text(text = it, color = Color.Red, fontSize = 14.sp)
            }

            success?.let {
                Text(text = it, color = Color(0xFF2E7D32), fontSize = 14.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

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
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(WebTeal.copy(0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.Home,
                                contentDescription = null,
                                tint = WebTeal,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                text = nombre.takeIf { it.isNotBlank() } ?: "Cargando...",
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp,
                                color = WebTeal
                            )
                            Text(email, color = Color.Gray, fontSize = 12.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // CAMPOS DEL PERFIL
                    ProfileInputField("Nombre", nombre, Icons.Default.Person, !editMode) { nombre = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Descripción", descripcion, Icons.Default.Info, !editMode) { descripcion = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("País", pais, Icons.Default.Public, !editMode) { pais = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Ciudad", ciudad, Icons.Default.LocationOn, !editMode) { ciudad = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Dirección", direccion, Icons.Default.Home, !editMode) { direccion = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Email", email, Icons.Default.Email, !editMode) { email = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Persona de contacto", personaContacto, Icons.Default.Person, !editMode) { personaContacto = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Teléfono", telefono, Icons.Default.Phone, !editMode, KeyboardType.Phone) { telefono = it }
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    ProfileInputField("Sitio Web", urlSitioWeb, Icons.Default.Language, !editMode) { urlSitioWeb = it }

                    Spacer(modifier = Modifier.height(32.dp))

                    // BOTONES
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        if (!editMode) {
                            Button(
                                onClick = { editMode = true },
                                colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Editar perfil", color = Color.White)
                            }
                        } else {
                            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                OutlinedButton(
                                    onClick = { editMode = false },
                                    shape = RoundedCornerShape(12.dp),
                                    colors = ButtonDefaults.outlinedButtonColors(contentColor = WebTeal)
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = {
                                        refugio?.let {
                                            viewModel.updateRefugio(
                                                refugioId = it.id,
                                                token = token,
                                                request = RefugioUpdateRequest(
                                                    nombre = nombre,
                                                    descripcion = descripcion,
                                                    pais = pais,
                                                    ciudad = ciudad,
                                                    direccion = direccion,
                                                    email = email,
                                                    personaContacto = personaContacto,
                                                    telefono = telefono,
                                                    urlSitioWeb = urlSitioWeb
                                                )
                                            )
                                        }
                                        editMode = false
                                    },
                                    colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Guardar", color = Color.White)
                                }
                            }
                        }
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
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    readOnly: Boolean,
    keyboard: KeyboardType = KeyboardType.Text,
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
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
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
