package com.example.matchpet.data.model.animal

data class Animal(
    val animal_id: Int,
    val nombre: String,
    val raza: String?,
    val genero: String?,
    val edad: Int?, // Agregado por si acaso
    val descripcionPersonalidad: String?,
    val estadoAdopcion: String,
    val refugioNombre: String?,
    val refugioCiudad: String?,
    val fotos: List<String>?
)

data class PageResponse<T>(
    val content: List<T>,
    val totalPages: Int,
    val totalElements: Long,
    val size: Int,
    val number: Int
)
