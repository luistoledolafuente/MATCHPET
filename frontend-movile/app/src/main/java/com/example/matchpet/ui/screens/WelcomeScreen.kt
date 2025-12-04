package com.example.matchpet.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.matchpet.ui.components.PrimaryButton
import com.example.matchpet.ui.components.SecondaryButton
import com.example.matchpet.ui.theme.*
import androidx.compose.ui.res.painterResource
import androidx.compose.foundation.Image
import com.example.matchpet.R

@Composable
fun WelcomeScreen(
    onAdoptClick: () -> Unit,
    onDonateClick: () -> Unit,
    onShelterClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                    colors = listOf(
                        WebBlueLight,
                        WebCream,
                        WebCream
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Scroll global para todo el contenido dentro del Card
        Column(
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .shadow(6.dp, shape = RoundedCornerShape(20.dp)),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = SurfaceWhite
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                Column(
                    modifier = Modifier
                        .padding(24.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Ícono del logo (patita)
                    Image(
                        painter = painterResource(id = R.drawable.logo_matchpet),
                        contentDescription = "Logo MatchPet",
                        modifier = Modifier.size(150.dp)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "MatchPet",
                        color = WebTeal,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.ExtraBold
                    )

                    Text(
                        text = "Encuentra a tu compañero ideal",
                        color = TextSecondary,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Botones principales
                    Button(
                        onClick = onAdoptClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WebTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(text = "Adoptar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Button(
                        onClick = onDonateClick,
                        colors = ButtonDefaults.buttonColors(containerColor = WebSalmon),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(text = "Donar", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    OutlinedButton(
                        onClick = onShelterClick,
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = WebTeal),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WebTeal),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth().height(50.dp)
                    ) {
                        Text(text = "Soy Refugio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }

                    Spacer(modifier = Modifier.height(28.dp))

                    // Sección informativa
                    Text(
                        text = "¿Cómo te ayuda MatchPet?",
                        color = WebTeal,
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Descubre cómo nuestra plataforma facilita la adopción y apoya a los refugios de animales.",
                        color = TextSecondary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Cards informativas con íconos integrados
                    InfoCard(
                        icon = Icons.Filled.Favorite,
                        title = "Adopta con Amor",
                        description = "Facilitamos el proceso de adopción conectándote con tu futura mascota de forma simple y amigable."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoCard(
                        icon = Icons.Filled.Psychology,
                        title = "Recomendaciones con IA",
                        description = "Nuestra inteligencia artificial analiza tu perfil para recomendarte la mascota ideal según tu estilo de vida."
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    InfoCard(
                        icon = Icons.Filled.Home,
                        title = "Apoya a Refugios",
                        description = "Cada adopción o donación contribuye directamente al bienestar de los animales en refugios."
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun InfoCard(icon: ImageVector, title: String, description: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(2.dp, shape = RoundedCornerShape(12.dp))
            .background(
                color = Color.White,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Circle background for icon
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(WebBlueLight.copy(alpha = 0.3f), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = WebTeal,
                modifier = Modifier.size(32.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = title,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = description,
            color = TextSecondary,
            fontSize = 14.sp,
            textAlign = TextAlign.Center
        )
    }
}
