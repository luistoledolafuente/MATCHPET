package com.example.matchpet.ui.screens
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.matchpet.data.model.UserProfileResponse
import com.example.matchpet.data.network.RetrofitClient
import com.example.matchpet.ui.theme.*

@Composable
fun ProfileScreen(token: String) {
    var profile by remember { mutableStateOf<UserProfileResponse?>(null) }
    var error by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getProfile("Bearer $token")
            if (response.isSuccessful) {
                profile = response.body()
            } else {
                error = "Error ${response.code()}: ${response.message()}"
            }

        } catch (e: Exception) {
            error = e.message
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        if (profile != null) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.Start
            ) {
                Text("Nombre: ${profile!!.nombreCompleto}", style = MaterialTheme.typography.bodyLarge)
                Text("Correo: ${profile!!.email}")
                Text("Teléfono: ${profile!!.telefono ?: "Sin número"}")
                Text("Rol: ${profile!!.role}")
            }
        } else if (error != null) {
            Text("Error: $error", color = ErrorRed)
        } else {
            CircularProgressIndicator(color = PrimaryTeal)
        }
    }
}
