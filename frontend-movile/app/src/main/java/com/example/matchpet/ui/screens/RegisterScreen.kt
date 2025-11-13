package com.example.matchpet.ui.screens

import androidx.compose.ui.platform.LocalContext
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.matchpet.data.model.UserRole
import com.example.matchpet.ui.components.PrimaryButton
import com.example.matchpet.ui.components.RoleSelector
import com.example.matchpet.ui.theme.*
import com.example.matchpet.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    onRegisterClick: () -> Unit,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val viewModel: AuthViewModel = viewModel()

    var selectedRole by remember { mutableStateOf(UserRole.ADOPTER) }
    var nombre by remember { mutableStateOf("") }
    var correo by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }

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
                    text = "Crea tu cuenta en MatchPet",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                RoleSelector(
                    selectedRole = selectedRole,
                    onRoleSelected = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Nombre
                OutlinedTextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text("Nombre completo") },
                    leadingIcon = { Icon(Icons.Filled.Person, contentDescription = null, tint = PrimaryTeal) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Correo
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo electrónico") },
                    leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = null, tint = PrimaryTeal) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Teléfono y fecha
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = telefono,
                        onValueChange = { telefono = it },
                        label = { Text("Teléfono") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BackgroundBeige,
                            unfocusedContainerColor = BackgroundBeige
                        )
                    )
                    OutlinedTextField(
                        value = fechaNacimiento,
                        onValueChange = { fechaNacimiento = it },
                        label = { Text("Fecha de Nacimiento") },
                        placeholder = { Text("DD/MM/AAAA") },
                        modifier = Modifier.weight(1f),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = BackgroundBeige,
                            unfocusedContainerColor = BackgroundBeige
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Ciudad
                OutlinedTextField(
                    value = ciudad,
                    onValueChange = { ciudad = it },
                    label = { Text("Ciudad") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Dirección
                OutlinedTextField(
                    value = direccion,
                    onValueChange = { direccion = it },
                    label = { Text("Dirección") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Contraseña
                OutlinedTextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text("Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = PrimaryTeal) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Confirmar contraseña
                OutlinedTextField(
                    value = confirmar,
                    onValueChange = { confirmar = it },
                    label = { Text("Confirmar Contraseña") },
                    leadingIcon = { Icon(Icons.Filled.Lock, contentDescription = null, tint = PrimaryTeal) },
                    visualTransformation = PasswordVisualTransformation(),
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = BackgroundBeige,
                        unfocusedContainerColor = BackgroundBeige
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registro
                PrimaryButton(
                    text = "Registrarme",
                    onClick = {
                        if (nombre.isBlank() || correo.isBlank() || contrasena.isBlank()) {
                            Toast.makeText(context, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(context, "Registrando usuario...", Toast.LENGTH_SHORT).show()

                            viewModel.register(
                                email = correo,
                                password = contrasena,
                                nombre = nombre,
                                telefono = telefono,
                                onSuccess = {
                                    Toast.makeText(context, "Registro exitoso ✅", Toast.LENGTH_LONG).show()
                                    onRegisterClick() // Navega o limpia campos
                                },
                                onError = { mensajeError ->
                                    Toast.makeText(context, "Error: $mensajeError", Toast.LENGTH_LONG).show()
                                }
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Enlace para ir al login
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "¿Ya tienes cuenta? Inicia sesión",
                        color = PrimaryTeal
                    )
                }
            }
        }
    }
}
