package com.example.matchpet.data.model

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

    val temperamentos: List<String>? = emptyList(),

    val fotos: List<String>? = emptyList()
)
