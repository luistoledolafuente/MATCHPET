package com.example.matchpet.ui.screens.adoptante

import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.matchpet.ui.theme.*
import com.example.matchpet.viewmodel.DonacionViewModel
import com.example.matchpet.viewmodel.DonationState
import java.math.BigDecimal

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DonacionScreen(
    navController: NavController,
    token: String,
    refugioId: Int? = null,
    refugioNombre: String? = null,
    viewModel: DonacionViewModel = viewModel()
) {
    val donationState by viewModel.donationState.collectAsState()
    val context = LocalContext.current

    var monto by remember { mutableStateOf("") }
    var mensaje by remember { mutableStateOf("") }
    var showWebView by remember { mutableStateOf(false) }
    var paymentUrl by remember { mutableStateOf("") }

    // Montos predefinidos
    val montosPredefinidos = listOf("5", "10", "20", "50", "100")

    // Manejar estado de checkout
    LaunchedEffect(donationState) {
        when (val state = donationState) {
            is DonationState.CheckoutReady -> {
                paymentUrl = state.checkoutResponse.url
                showWebView = true
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Donar", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Filled.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PrimaryTeal)
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(BackgroundLight)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Icono de corazón
                Icon(
                    imageVector = Icons.Filled.Favorite,
                    contentDescription = null,
                    tint = SecondaryPink,
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (refugioNombre != null) "Donar a $refugioNombre" else "Hacer una donación",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "Tu donación ayuda a los animales a encontrar un hogar",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Montos predefinidos
                Text(
                    text = "Selecciona un monto",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Montos en dos filas para que no se corten
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("5", "10", "20").forEach { montoPredef ->
                        FilterChip(
                            selected = monto == montoPredef,
                            onClick = { monto = montoPredef },
                            label = { Text("S/ $montoPredef") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    listOf("50", "100", "200").forEach { montoPredef ->
                        FilterChip(
                            selected = monto == montoPredef,
                            onClick = { monto = montoPredef },
                            label = { Text("S/ $montoPredef") },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = PrimaryTeal,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para monto personalizado
                OutlinedTextField(
                    value = monto,
                    onValueChange = { monto = it.filter { c -> c.isDigit() || c == '.' } },
                    label = { Text("Monto personalizado (S/)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    )
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Campo para mensaje (opcional)
                OutlinedTextField(
                    value = mensaje,
                    onValueChange = { mensaje = it },
                    label = { Text("Mensaje (opcional)") },
                    maxLines = 3,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceWhite,
                        unfocusedContainerColor = SurfaceWhite
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Botón de donar
                Button(
                    onClick = {
                        val montoDecimal = monto.toBigDecimalOrNull()
                        if (montoDecimal != null && montoDecimal >= BigDecimal.ONE) {
                            viewModel.createDonation(
                                monto = montoDecimal,
                                refugioId = refugioId,
                                mensaje = mensaje.takeIf { it.isNotBlank() },
                                token = token
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryTeal),
                    enabled = monto.isNotBlank() && 
                              (monto.toBigDecimalOrNull() ?: BigDecimal.ZERO) >= BigDecimal.ONE &&
                              donationState !is DonationState.Loading
                ) {
                    if (donationState is DonationState.Loading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(
                            text = "Donar S/ ${monto.ifBlank { "0" }}",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                // Mostrar error si hay
                if (donationState is DonationState.Error) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = (donationState as DonationState.Error).message,
                        color = ErrorRed,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                // Información de seguridad
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = BackgroundBeige),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "🔒 Pago seguro con Mercado Pago",
                            fontWeight = FontWeight.Medium,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Tu información está protegida. Aceptamos tarjetas de crédito, débito y otros métodos de pago.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        }
    }

    // WebView Dialog para Mercado Pago
    if (showWebView && paymentUrl.isNotBlank()) {
        Dialog(
            onDismissRequest = { 
                showWebView = false
                viewModel.resetState()
            },
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
            ) {
                AndroidView(
                    factory = { ctx ->
                        WebView(ctx).apply {
                            settings.javaScriptEnabled = true
                            settings.domStorageEnabled = true
                            
                            webViewClient = object : WebViewClient() {
                                override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                                    super.onPageStarted(view, url, favicon)
                                    Log.d("DonacionScreen", "Loading: $url")
                                    
                                    // Detectar URLs de éxito o fallo (ajustar según tu configuración)
                                    url?.let {
                                        when {
                                            it.contains("success") || it.contains("approved") -> {
                                                showWebView = false
                                                viewModel.resetState()
                                                navController.popBackStack()
                                            }
                                            it.contains("failure") || it.contains("rejected") -> {
                                                showWebView = false
                                                viewModel.resetState()
                                            }
                                        }
                                    }
                                }
                            }
                            loadUrl(paymentUrl)
                        }
                    },
                )
            }
        }
    }
}
