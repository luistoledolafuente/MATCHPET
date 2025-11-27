package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchpet.data.model.RefugioUpdateRequest
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.data.repository.RefugioRepository
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.viewmodel.RefugioViewModel
import com.example.matchpet.viewmodel.RefugioViewModelFactory

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

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        item {
            // HEADER
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onBack() }) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver",
                        tint = PrimaryTeal
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "Perfil del Refugio",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        item {
            // FOTO / ICONO DE PERFIL
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(PrimaryTeal.copy(0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Default.AccountCircle,
                    contentDescription = null,
                    tint = PrimaryTeal,
                    modifier = Modifier.size(90.dp)
                )
            }
        }

        item { Spacer(Modifier.height(16.dp)) }

        item {
            Text(
                text = nombre.takeIf { it.isNotBlank() } ?: "Cargando...",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
            Text(email, color = Color.Gray, fontSize = 14.sp)
        }

        item { Spacer(Modifier.height(20.dp)) }

        if (loading) {
            item { CircularProgressIndicator(color = PrimaryTeal) }
        }

        error?.let {
            item { Text(text = it, color = Color.Red, fontSize = 14.sp) }
        }

        success?.let {
            item { Text(text = it, color = Color(0xFF2E7D32), fontSize = 14.sp) }
        }

        item { Spacer(Modifier.height(20.dp)) }

        // CAMPOS DEL PERFIL
        item { ProfileInputField("Nombre", nombre, { nombre = it }, editMode) }
        item { ProfileInputField("Descripción", descripcion, { descripcion = it }, editMode) }
        item { ProfileInputField("País", pais, { pais = it }, editMode) }
        item { ProfileInputField("Ciudad", ciudad, { ciudad = it }, editMode) }
        item { ProfileInputField("Dirección", direccion, { direccion = it }, editMode) }
        item { ProfileInputField("Email", email, { email = it }, editMode) }
        item { ProfileInputField("Persona de contacto", personaContacto, { personaContacto = it }, editMode) }
        item { ProfileInputField("Teléfono", telefono, { telefono = it }, editMode, KeyboardType.Phone) }
        item { ProfileInputField("Sitio Web", urlSitioWeb, { urlSitioWeb = it }, editMode) }

        item { Spacer(Modifier.height(25.dp)) }

        // BOTONES
        item {
            if (!editMode) {
                Button(
                    onClick = { editMode = true },
                    colors = ButtonDefaults.buttonColors(PrimaryTeal)
                ) {
                    Text("Editar perfil", color = Color.White)
                }
            } else {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    OutlinedButton(onClick = { editMode = false }) {
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
                        colors = ButtonDefaults.buttonColors(PrimaryTeal)
                    ) {
                        Text("Guardar cambios", color = Color.White)
                    }
                }
            }
        }
    }
}


@Composable
fun ProfileInputField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean,
    keyboard: KeyboardType = KeyboardType.Text
) {
    Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
        Text(label, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            enabled = enabled,
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = keyboard),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PrimaryTeal,
                cursorColor = PrimaryTeal
            )
        )
    }
}
