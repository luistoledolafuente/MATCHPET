package com.example.matchpet.data.model.animal

import com.google.gson.annotations.SerializedName

// Modelos específicos para entidades con nombres de campo diferentes

data class EspecieItem(
    val id: Int,
    @SerializedName("nombreEspecie")
    val nombre: String
)

data class RazaItem(
    val id: Int,
    @SerializedName("nombreRaza")
    val nombre: String,
    // Incluimos la referencia a especie por si la necesitamos
    val especie: EspecieItem? = null
)

data class TemperamentoItem(
    val id: Int,
    @SerializedName("nombreTemperamento")
    val nombre: String
)
