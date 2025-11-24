package com.example.matchpet.ui.screens.auth

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matchpet.data.model.UserRole
import com.example.matchpet.ui.components.GoogleSignInButton
import com.example.matchpet.ui.components.PrimaryButton
import com.example.matchpet.ui.components.RoleSelector
import com.example.matchpet.ui.theme.*
import com.example.matchpet.viewmodel.AuthState
import com.example.matchpet.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    navController: NavController,
    onRegisterClick: () -> Unit,
    viewModel: AuthViewModel = viewModel() // 🔑 Inyectar ViewModel
) {
    val authState by viewModel.authState.collectAsState()

    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf(UserRole.ADOPTER) }
    // No necesitamos errorMessage local, usamos el estado del ViewModel (authState)

    // Manejo de la navegación y errores basado en el estado
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                // Navegación por rol
                val destination = when (state.userRole) {
                    UserRole.ADOPTER -> "adoptante/dashboard/${state.token}"
                    UserRole.SHELTER -> "refugio/dashboard/${state.token}"
                }
                navController.navigate(destination) {
                    // Limpiar la pila de navegación para que no puedan volver al login
                    popUpTo("login") { inclusive = true }
                }
                viewModel.resetState() // Resetear estado
            }
            is AuthState.Error -> {
                Log.e("LoginScreen", "Login Fallido: ${state.message}")
                // El error se mostrará en el Text de abajo
            }
            else -> {}
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundLight),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth()
                .shadow(6.dp, shape = RoundedCornerShape(20.dp)),
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceWhite),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Bienvenido a MatchPet",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Rol (aunque el login es universal, mantienes la UI)
                RoleSelector(selectedRole = selectedRole, onRoleSelected = { selectedRole = it })

                Spacer(modifier = Modifier.height(16.dp))

                // Campo correo
                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = null, tint = PrimaryTeal) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    // ✅ CORRECCIÓN: Usar OutlinedTextFieldDefaults.colors
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    ),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Campo contraseña
                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = PrimaryTeal) },
                    visualTransformation = PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    // ✅ CORRECCIÓN: Usar OutlinedTextFieldDefaults.colors
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "¿Olvidaste tu contraseña?",
                    color = TextSecondary,
                    textAlign = TextAlign.End,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Botón principal de login
                PrimaryButton(
                    text = "Iniciar Sesión",
                    onClick = {
                        viewModel.login(email, password)
                    },
                    isLoading = authState is AuthState.Loading,
                    enabled = email.isNotBlank() && password.isNotBlank() && authState !is AuthState.Loading
                )


                // Mostrar error si hay
                if (authState is AuthState.Error) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = (authState as AuthState.Error).message,
                        color = ErrorRed,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                GoogleSignInButton(onClick = { /* TODO: Google Auth */ })

                Spacer(modifier = Modifier.height(20.dp))

                TextButton(onClick = onRegisterClick) {
                    Text(
                        text = "¿No tienes cuenta? Regístrate aquí",
                        color = PrimaryTeal
                    )
                }
            }
        }
    }
}