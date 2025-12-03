package com.example.matchpet.ui.screens.refugio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.matchpet.data.model.animal.AnimalResponse
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.Resource
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.refugio.RefugioViewModel
import kotlinx.coroutines.flow.collectLatest

/* ---------------------------------------------------------
 *               DASHBOARD — DATA CLASS
 * --------------------------------------------------------- */
data class DashboardCardData(
    val title: String,
    val value: Int,
    val max: Int,
    val accentColor: Color,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

/* ---------------------------------------------------------
 *             DASHBOARD — TARJETAS (2 POR FILA)
 * --------------------------------------------------------- */
@Composable
fun MetricCardMinimal(card: DashboardCardData) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFFAF3)),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(card.icon, contentDescription = null, tint = Color(0xFF2B6777))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = card.title,
                    color = Color(0xFF2B6777),
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Text(
                text = card.value.toString(),
                fontSize = 32.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF2B6777)
            )

            val progress = if (card.max == 0) 0f else (card.value.toFloat() / card.max)

            LinearProgressIndicator(
                progress = progress,
                color = card.accentColor,
                trackColor = Color(0xFFD9F4FF),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
        }
    }
}

/* ---------------------------------------------------------
 *                TARJETA DE ANIMAL RECIENTE
 * --------------------------------------------------------- */
@Composable
fun RecentAnimalCard(animal: AnimalResponse) {

    val foto = animal.fotos?.firstOrNull()
    val fullUrl =
        if (foto != null && foto.startsWith("/"))
            "${Injection.BACKEND_BASE_URL}$foto"
        else
            foto ?: "https://placehold.co/200x200?text=NoFoto"

    Card(
        modifier = Modifier
            .width(150.dp)
            .height(190.dp),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            Image(
                painter = rememberAsyncImagePainter(fullUrl),
                contentDescription = "Foto de ${animal.nombre}",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(topStart = 18.dp, topEnd = 18.dp))
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = animal.nombre,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color(0xFF2B6777)
            )

            Text(
                text = animal.raza,
                fontSize = 13.sp,
                color = Color.Gray
            )
        }
    }
}

/* ---------------------------------------------------------
 *                    PANTALLA PRINCIPAL
 * --------------------------------------------------------- */
@Composable
fun RefugioHomeScreen(
    navController: NavController,
    token: String,
    refugioViewModel: RefugioViewModel,
    animalRepository: AnimalRepository
) {

    var total by remember { mutableStateOf(0) }
    var adoptadas by remember { mutableStateOf(0) }
    var disponibles by remember { mutableStateOf(0) }
    var enProceso by remember { mutableStateOf(0) }
    var machos by remember { mutableStateOf(0) }
    var hembras by remember { mutableStateOf(0) }

    var isLoading by remember { mutableStateOf(true) }
    var error: String? by remember { mutableStateOf(null) }

    var animalesRecientes by remember { mutableStateOf<List<AnimalResponse>>(emptyList()) }

    // Cargar datos
    LaunchedEffect(Unit) {
        refugioViewModel.loadProfile(token)

        animalRepository.getMisAnimales(token).collectLatest { resource ->

            isLoading = resource is Resource.Loading

            when (resource) {
                is Resource.Success -> {
                    val mascotas = resource.data ?: emptyList()

                    animalesRecientes = mascotas.takeLast(10)

                    total = mascotas.size
                    adoptadas = mascotas.count { it.estadoAdopcion.equals("Adoptada", true) }
                    disponibles = mascotas.count { it.estadoAdopcion.equals("Disponible", true) }

                    enProceso = mascotas.count {
                        val s = it.estadoAdopcion.lowercase()
                        s.contains("proceso") || s.contains("cuidado") || s.contains("acogida")
                    }

                    machos = mascotas.count { it.genero.equals("Macho", true) }
                    hembras = mascotas.count { it.genero.equals("Hembra", true) }

                    error = null
                }

                is Resource.Error -> error = resource.message
                else -> {}
            }
        }
    }

    val refugioProfile by refugioViewModel.refugio.collectAsState()

    /* -------------------------------------------
     *                UI PRINCIPAL
     * ------------------------------------------- */
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF4FCFF))
    ) {
        // HEADER
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFF2B6777))
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("¡Hola!", color = Color.White, fontSize = 22.sp)
            Text(
                refugioProfile?.nombre ?: "Refugio",
                fontSize = 28.sp,
                color = Color.White,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                "Dashboard de mascotas",
                fontSize = 14.sp,
                color = Color(0xFFDAF3F8)
            )
            Spacer(Modifier.height(16.dp))
        }

        Spacer(Modifier.height(16.dp))

        if (isLoading) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF2B6777))
            }
            return@Column
        }

        if (error != null) {
            Box(Modifier.fillMaxSize(), Alignment.Center) {
                Text("Error: $error", color = Color.Red)
            }
            return@Column
        }

        // CARDS DEL DASHBOARD
        val cards = listOf(
            DashboardCardData("Total Mascotas", total, total, Color(0xFFFFD6A5), Icons.Filled.Pets),
            DashboardCardData("Adoptadas", adoptadas, total, Color(0xFFA0C4FF), Icons.Filled.Verified),
            DashboardCardData("Disponibles", disponibles, total, Color(0xFFA8E6CF), Icons.Filled.FavoriteBorder),
            DashboardCardData("En Proceso", enProceso, total, Color(0xFFFFC6A5), Icons.Filled.Schedule),
            DashboardCardData("Machos", machos, total, Color(0xFFFFE1A8), Icons.Filled.Male),
            DashboardCardData("Hembras", hembras, total, Color(0xFFCDB4DB), Icons.Filled.Female)
        )

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(12.dp)
        ) {
            items(cards) { card ->
                MetricCardMinimal(card)
            }
        }
        Spacer(Modifier.height(16.dp))

        // ANIMALES RECIENTES
        Text(
            "Animales recién añadidos",
            fontWeight = FontWeight.Bold,
            color = Color(0xFF2B6777),
            fontSize = 20.sp,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
        )

        LazyRow(
            modifier = Modifier.padding(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(animalesRecientes) { animal ->
                RecentAnimalCard(animal)
            }
        }
    }
}
