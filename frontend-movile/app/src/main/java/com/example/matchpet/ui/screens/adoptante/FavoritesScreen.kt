package com.example.matchpet.ui.screens.adoptante

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.matchpet.data.model.animal.Animal
import com.example.matchpet.ui.theme.PaleTeal
import com.example.matchpet.ui.theme.WebTeal
import com.example.matchpet.utils.Injection
import com.example.matchpet.viewmodel.adoptante.FavoritesViewModel
import com.example.matchpet.ui.components.PetCard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    token: String,
    navController: NavController,
    viewModel: FavoritesViewModel = viewModel(factory = Injection.provideFavoritesViewModelFactory())
) {
    val favorites by viewModel.favorites.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.loadFavorites(token)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mis Favoritos") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = WebTeal,
                    titleContentColor = androidx.compose.ui.graphics.Color.White,
                    navigationIconContentColor = androidx.compose.ui.graphics.Color.White
                )
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues).fillMaxSize().background(PaleTeal)) {
            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (error != null) {
                Text(
                    text = error ?: "Error desconocido",
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (favorites.isEmpty()) {
                Text(
                    text = "No tienes favoritos aún.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(favorites) { animal ->
                        PetCard(
                            animal = animal,
                            isFavorite = true,
                            onToggleFavorite = {
                                viewModel.removeFavorite(token, animal.animal_id)
                            },
                            onViewProfile = {
                                navController.navigate("animal_detail/${animal.animal_id}")
                            }
                        )
                    }
                }
            }
        }
    }
}


