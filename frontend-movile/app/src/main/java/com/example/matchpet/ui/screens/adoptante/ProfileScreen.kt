package com.example.matchpet.ui.screens.adoptante

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.matchpet.data.model.UserProfileResponse
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.PrimaryTeal
import com.example.matchpet.ui.theme.ErrorRed

@Composable
fun ProfileScreen(
    token: String,
    onBack: () -> Unit = {}
) {
    var profile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    // Campos editables basados SOLO en tu modelo real
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }

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
                    ciudad = user.ciudad ?: ""
                }
            } else {
                error = "Error ${response.code()}: ${response.message()}"
            }

        } catch (e: Exception) {
            error = e.message
        }
    }

    // --- UI ---
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF9F1))
            .verticalScroll(scroll)
            .padding(20.dp)
    ) {

        // HEADER
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Gestionar Perfil",
                fontSize = 22.sp,
                color = Color(0xFF244B57)
            )

            IconButton(onClick = { onBack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = PrimaryTeal
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (error != null) {
            Text("Error: $error", color = ErrorRed)
            return
        }

        if (profile == null) {
            CircularProgressIndicator(color = PrimaryTeal)
            return
        }

        // CARD
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(4.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp)) {

                // FOTO DE PERFIL
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {

                    AsyncImage(
                        model = "https://ui-avatars.com/api/?name=${nombre}+${apellidoPaterno}",
                        contentDescription = "Foto",
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                    )

                    TextButton(onClick = {
                        Toast.makeText(context, "Aquí cargarás la foto local", Toast.LENGTH_SHORT).show()
                    }) {
                        Text("Cambiar foto", color = PrimaryTeal)
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // NOMBRE
                ProfileInputField(
                    label = "Nombre",
                    value = nombre,
                    onValueChange = { nombre = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // APELLIDO PATERNO
                ProfileInputField(
                    label = "Apellido Paterno",
                    value = apellidoPaterno,
                    onValueChange = { apellidoPaterno = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // APELLIDO MATERNO
                ProfileInputField(
                    label = "Apellido Materno",
                    value = apellidoMaterno,
                    onValueChange = { apellidoMaterno = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // EMAIL (solo lectura)
                ProfileInputField(
                    label = "Correo",
                    value = profile!!.email,
                    onValueChange = {},
                    readOnly = true
                )

                Spacer(modifier = Modifier.height(10.dp))

                // TELEFONO
                ProfileInputField(
                    label = "Teléfono",
                    value = telefono,
                    onValueChange = { telefono = it }
                )

                Spacer(modifier = Modifier.height(10.dp))

                // CIUDAD (solo si existe en tu modelo)
                ProfileInputField(
                    label = "Ciudad",
                    value = ciudad,
                    onValueChange = { ciudad = it }
                )

                Spacer(modifier = Modifier.height(20.dp))

                Button(
                    onClick = {
                        Toast.makeText(context, "Cambios guardados", Toast.LENGTH_SHORT).show()
                        // Regresar al dashboard inicial después de guardar
                        onBack()
                    },
                    modifier = Modifier
                        .align(Alignment.End)
                        .height(45.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal)
                ) {
                    Text("Guardar Cambios", color = Color.White)
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
    readOnly: Boolean = false
) {
    Column {
        Text(label, fontSize = 14.sp, color = Color(0xFF6F6F6F))
        OutlinedTextField(
            value = value,
            onValueChange = { if (!readOnly) onValueChange(it) },
            modifier = Modifier.fillMaxWidth(),
            readOnly = readOnly,
            singleLine = true
        )
    }
}
