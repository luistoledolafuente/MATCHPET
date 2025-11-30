package com.example.matchpet.data.model.animal

import com.google.gson.annotations.SerializedName

data class AnimalResponse(
    @SerializedName("animal_id")
    val id: Int,

    val nombre: String,
    val fechaNacimientoAprox: String? = null,

    @SerializedName("descripcionPersonalidad")
    val descripcionPersonalidad: String? = null,

    @SerializedName("compatibleNiños")
    val compatibleNinos: Boolean,

    @SerializedName("compatibleOtrasMascotas")
    val compatibleOtrasMascotas: Boolean,

    @SerializedName("estaVacunado")
    val estaVacunado: Boolean,

    @SerializedName("estaEsterilizado")
    val estaEsterilizado: Boolean,

    val historialMedico: String? = null,
    val fechaIngresoRefugio: String? = null,

    // 🔑 CAMPOS AÑADIDOS para cargar la edición (el ViewModel los necesita)
    val especieId: Int? = null,
    val razaId: Int? = null,
    val generoId: Int? = null,
    val tamanoId: Int? = null,
    val nivelEnergiaId: Int? = null,

    @SerializedName("estadoAdopcionId") // Asumo que el backend también envía el ID
    val estadoAdopcionId: Int? = null,

    // 🔑 Listas y Fotos requeridas por el ViewModel
    @SerializedName("temperamentosIds")
    val temperamentosIds: List<Int>? = emptyList(),

    @SerializedName("fotoPrincipalIndex")
    val fotoPrincipalIndex: Int? = 0,


    // --- Campos de String Originales (los mantenemos por compatibilidad) ---
    val raza: String,
    val especie: String,
    val genero: String,
    val tamano: String,
    val nivelEnergia: String? = null,

    @SerializedName("estadoAdopcion")
    val estadoAdopcion: String,

    @SerializedName("refugioNombre")
    val refugioNombre: String? = null,

    @SerializedName("refugioCiudad")
    val refugioCiudad: String? = null,

    val temperamentos: List<String>? = emptyList(), // Lista de nombres de temperamentos

    val fotos: List<String>? = emptyList() // Lista de URLs de fotos
)