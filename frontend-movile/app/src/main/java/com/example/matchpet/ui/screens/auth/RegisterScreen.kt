package com.example.matchpet.ui.screens.auth

import android.util.Log
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Star
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
import androidx.navigation.NavController
import com.example.matchpet.data.model.auth.UserRole
import com.example.matchpet.ui.components.PrimaryButton
import com.example.matchpet.ui.components.RoleSelector
import com.example.matchpet.ui.theme.*
import com.example.matchpet.viewmodel.AuthState
import com.example.matchpet.viewmodel.AuthViewModel

@Composable
fun RegisterScreen(
    navController: NavController,
    onLoginClick: () -> Unit,
    viewModel: AuthViewModel = viewModel()
) {
    val context = LocalContext.current
    val authState by viewModel.authState.collectAsState()

    var selectedRole by remember { mutableStateOf(UserRole.ADOPTER) }

    // --- Campos Comunes (para login, password, email) ---
    var correo by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }
    var confirmar by remember { mutableStateOf("") }
    var ciudad by remember { mutableStateOf("") }
    var pais by remember { mutableStateOf("") }

    // --- Campos de Adoptante ---
    var nombre by remember { mutableStateOf("") }
    var apellidoPaterno by remember { mutableStateOf("") }
    var apellidoMaterno by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }
    var direccion by remember { mutableStateOf("") }
    var fechaNacimiento by remember { mutableStateOf("2000-01-01") }

    // --- Campos de Refugio ---
    var nombreRefugio by remember { mutableStateOf("") }
    var descripcionRefugio by remember { mutableStateOf("") }
    var emailRefugio by remember { mutableStateOf("") }
    var personaContacto by remember { mutableStateOf("") }
    var telefonoContacto by remember { mutableStateOf("") }
    var urlSitioWeb by remember { mutableStateOf("") }


    // 🔑 Lógica de Navegación basada en el estado
    LaunchedEffect(authState) {
        when (val state = authState) {
            is AuthState.Success -> {
                val destination = when (state.userRole) {
                    UserRole.ADOPTER -> "adoptante/dashboard/${state.token}"
                    UserRole.SHELTER -> "refugio/dashboard/${state.token}"
                }
                Toast.makeText(context, "Registro exitoso. Bienvenido!", Toast.LENGTH_SHORT).show()
                navController.navigate(destination) {
                    popUpTo("register") { inclusive = true }
                }
                viewModel.resetState()
            }
            is AuthState.Error -> {
                Toast.makeText(context, "Error de registro: ${state.message}", Toast.LENGTH_LONG).show()
                viewModel.resetState()
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
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Crea tu cuenta en MatchPet",
                    color = TextPrimary,
                    style = MaterialTheme.typography.titleLarge,
                    fontSize = 22.sp
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Selector de Rol
                RoleSelector(
                    selectedRole = selectedRole,
                    onRoleSelected = { selectedRole = it }
                )

                Spacer(modifier = Modifier.height(16.dp))

                // ----------------------------------------------------
                // 1. CAMPOS DE AUTENTICACIÓN (Comunes a ambos)
                // ----------------------------------------------------

                // Correo (para Login)
                OutlinedTextField(
                    value = correo,
                    onValueChange = { correo = it },
                    label = { Text("Correo Electrónico (Para Iniciar Sesión)") },
                    leadingIcon = { Icon(Icons.Filled.MailOutline, contentDescription = null, tint = PrimaryTeal) },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige)
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige)
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
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige)
                )
                Spacer(modifier = Modifier.height(16.dp))

                // ----------------------------------------------------
                // 2. CAMPOS ESPECÍFICOS SEGÚN EL ROL
                // ----------------------------------------------------

                // Animación simple para los campos específicos
                AnimatedContent(
                    targetState = selectedRole,
                    transitionSpec = {
                        slideInVertically() + fadeIn() togetherWith slideOutVertically() + fadeOut()
                    }
                ) { targetRole ->
                    Column(modifier = Modifier.fillMaxWidth()) {
                        when (targetRole) {
                            UserRole.ADOPTER -> AdoptanteFields(
                                nombre, { nombre = it },
                                apellidoPaterno, { apellidoPaterno = it },
                                apellidoMaterno, { apellidoMaterno = it },
                                telefono, { telefono = it },
                                direccion, { direccion = it },
                                ciudad, { ciudad = it },
                                pais, { pais = it },
                                fechaNacimiento, { fechaNacimiento = it }
                            )
                            UserRole.SHELTER -> RefugioFields(
                                nombreRefugio, { nombreRefugio = it },
                                descripcionRefugio, { descripcionRefugio = it },
                                emailRefugio, { emailRefugio = it },
                                personaContacto, { personaContacto = it },
                                telefonoContacto, { telefonoContacto = it },
                                urlSitioWeb, { urlSitioWeb = it },
                                direccion, { direccion = it },
                                ciudad, { ciudad = it },
                                pais, { pais = it }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Botón de registro
                PrimaryButton(
                    text = "Registrarme",
                    onClick = {
                        if (contrasena != confirmar) {
                            Toast.makeText(context, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                            return@PrimaryButton
                        }
                        handleRegistration(
                            viewModel, selectedRole,
                            // Comunes
                            correo, contrasena, ciudad, pais, direccion,
                            // Adoptante
                            nombre, apellidoPaterno, apellidoMaterno, telefono, fechaNacimiento,
                            // Refugio
                            nombreRefugio, descripcionRefugio, emailRefugio, personaContacto, telefonoContacto, urlSitioWeb
                        )
                    },
                    isLoading = authState is AuthState.Loading,
                    enabled = isRegistrationValid(selectedRole, contrasena, confirmar, correo, nombre, nombreRefugio) // Validación simple
                )

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(onClick = onLoginClick) {
                    Text(text = "¿Ya tienes cuenta? Inicia sesión", color = PrimaryTeal)
                }
            }
        }
    }
}

// ----------------------------------------------------
// FUNCIONES AUXILIARES Y COMPOSABLES
// ----------------------------------------------------

// Lógica de validación simple
private fun isRegistrationValid(
    role: UserRole, contrasena: String, confirmar: String, email: String,
    nombre: String, nombreRefugio: String
): Boolean {
    val commonValid = contrasena == confirmar && email.isNotBlank() && contrasena.isNotBlank()
    return commonValid && when (role) {
        UserRole.ADOPTER -> nombre.isNotBlank()
        UserRole.SHELTER -> nombreRefugio.isNotBlank()
    }
}

// Lógica para llamar al ViewModel
private fun handleRegistration(
    viewModel: AuthViewModel, role: UserRole,
    correo: String, contrasena: String, ciudad: String, pais: String, direccion: String,
    nombre: String, apPaterno: String, apMaterno: String, telefono: String, fechaNacimiento: String,
    nombreRefugio: String, descripcion: String, emailRefugio: String, personaContacto: String, telContacto: String, urlWeb: String
) {
    if (role == UserRole.ADOPTER) {
        viewModel.registerAdoptante(
            email = correo, password = contrasena, nombre = nombre, telefono = telefono,
            apellidoPaterno = apPaterno, apellidoMaterno = apMaterno, fechaNacimiento = fechaNacimiento,
            direccion = direccion, ciudad = ciudad, pais = pais
        )
    } else { // SHELTER
        viewModel.registerRefugio(
            emailLogin = correo, password = contrasena, nombreRefugio = nombreRefugio,
            descripcion = descripcion, direccion = direccion, ciudad = ciudad, pais = pais,
            emailRefugio = emailRefugio, personaContacto = personaContacto,
            telefonoContacto = telContacto, urlSitioWeb = if (urlWeb.isBlank()) null else urlWeb
        )
    }
}


@Composable
fun AdoptanteFields(
    nombre: String, onNombreChange: (String) -> Unit,
    apPaterno: String, onApPaternoChange: (String) -> Unit,
    apMaterno: String, onApMaternoChange: (String) -> Unit,
    telefono: String, onTelefonoChange: (String) -> Unit,
    direccion: String, onDireccionChange: (String) -> Unit,
    ciudad: String, onCiudadChange: (String) -> Unit,
    pais: String, onPaisChange: (String) -> Unit,
    fechaNacimiento: String, onFechaNacimientoChange: (String) -> Unit
) {
    // Nombre
    OutlinedTextField(value = nombre, onValueChange = onNombreChange, label = { Text("Nombre") }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Apellido Paterno
    OutlinedTextField(value = apPaterno, onValueChange = onApPaternoChange, label = { Text("Apellido Paterno") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Apellido Materno
    OutlinedTextField(value = apMaterno, onValueChange = onApMaternoChange, label = { Text("Apellido Materno") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Teléfono
    OutlinedTextField(value = telefono, onValueChange = onTelefonoChange, label = { Text("Teléfono") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Dirección
    OutlinedTextField(value = direccion, onValueChange = onDireccionChange, label = { Text("Dirección") }, leadingIcon = { Icon(Icons.Filled.Home, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Ciudad
    OutlinedTextField(value = ciudad, onValueChange = onCiudadChange, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // País
    OutlinedTextField(value = pais, onValueChange = onPaisChange, label = { Text("País") }, leadingIcon = { Icon(Icons.Filled.Public, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Fecha de Nacimiento (YYYY-MM-DD)
    OutlinedTextField(value = fechaNacimiento, onValueChange = onFechaNacimientoChange, label = { Text("Fecha Nac. (YYYY-MM-DD)") }, modifier = Modifier.fillMaxWidth(), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
}


@Composable
fun RefugioFields(
    nombreRefugio: String, onNombreRefugioChange: (String) -> Unit,
    descripcion: String, onDescripcionChange: (String) -> Unit,
    emailRefugio: String, onEmailRefugioChange: (String) -> Unit,
    personaContacto: String, onPersonaContactoChange: (String) -> Unit,
    telefonoContacto: String, onTelefonoContactoChange: (String) -> Unit,
    urlSitioWeb: String, onUrlSitioWebChange: (String) -> Unit,
    direccion: String, onDireccionChange: (String) -> Unit,
    ciudad: String, onCiudadChange: (String) -> Unit,
    pais: String, onPaisChange: (String) -> Unit,
) {
    // Nombre Refugio
    OutlinedTextField(value = nombreRefugio, onValueChange = onNombreRefugioChange, label = { Text("Nombre del Refugio") }, leadingIcon = { Icon(Icons.Filled.Star, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), // ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // Descripción
    OutlinedTextField(value = descripcion, onValueChange = onDescripcionChange, label = { Text("Descripción del Refugio") }, leadingIcon = { Icon(Icons.Filled.Description, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(),// ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // Email Refugio (Contacto)
    OutlinedTextField(value = emailRefugio, onValueChange = onEmailRefugioChange, label = { Text("Email de Contacto del Refugio") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email), modifier = Modifier.fillMaxWidth(), // ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // Persona de Contacto
    OutlinedTextField(value = personaContacto, onValueChange = onPersonaContactoChange, label = { Text("Persona de Contacto") }, leadingIcon = { Icon(Icons.Filled.Person, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Teléfono de Contacto
    OutlinedTextField(value = telefonoContacto, onValueChange = onTelefonoContactoChange, label = { Text("Teléfono de Contacto") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone), modifier = Modifier.fillMaxWidth(), // ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // URL Sitio Web
    OutlinedTextField(value = urlSitioWeb, onValueChange = onUrlSitioWebChange, label = { Text("URL Sitio Web (Opcional)") }, keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri), modifier = Modifier.fillMaxWidth(), // ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // Dirección
    OutlinedTextField(value = direccion, onValueChange = onDireccionChange, label = { Text("Dirección") }, leadingIcon = { Icon(Icons.Filled.Home, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
    Spacer(modifier = Modifier.height(8.dp))
    // Ciudad
    OutlinedTextField(value = ciudad, onValueChange = onCiudadChange, label = { Text("Ciudad") }, modifier = Modifier.fillMaxWidth(),// ✅ Código corregido (API actual de Material 3)
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = BackgroundBeige,
            unfocusedContainerColor = BackgroundBeige
        ))
    Spacer(modifier = Modifier.height(8.dp))
    // País
    OutlinedTextField(value = pais, onPaisChange, label = { Text("País") }, leadingIcon = { Icon(Icons.Filled.Public, null, tint = PrimaryTeal) }, modifier = Modifier.fillMaxWidth(), colors = TextFieldDefaults.colors(focusedContainerColor = BackgroundBeige, unfocusedContainerColor = BackgroundBeige))
}