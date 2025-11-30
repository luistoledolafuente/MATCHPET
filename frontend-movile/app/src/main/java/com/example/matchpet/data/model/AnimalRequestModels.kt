package com.example.matchpet.data.model

import com.google.gson.annotations.SerializedName

// Modelo para el registro de un nuevo animal (POST /api/animales)
data class AnimalCreationRequest(
    val nombre: String,
    val fechaNacimientoAprox: String? = null,
    val descripcionPersonalidad: String? = null,

    // [Booleans sin cambios]
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

    // 🔑 CAMPOS CLAVE: Ahora son IDs (Int)
    val especieId: Int, // Asumo que es requerido
    val razaId: Int,
    val generoId: Int,
    val tamanoId: Int,
    val nivelEnergiaId: Int,

    @SerializedName("estadoAdopcionId")
    val estadoAdopcionId: Int,

    // 🔑 Listas corregidas: IDs y nombre
    @SerializedName("temperamentosIds")
    val temperamentosIds: List<Int>? = emptyList(),

    @SerializedName("fotosUrls")
    val fotosUrls: List<String>? = emptyList(),

    @SerializedName("fotoPrincipalIndex")
    val fotoPrincipalIndex: Int = 0
)

data class AnimalUpdateRequest(
    val nombre: String? = null,
    val fechaNacimientoAprox: String? = null,
    val descripcionPersonalidad: String? = null,

    // 🔑 Booleanos COMPLETOS Y NULLABLE
    @SerializedName("compatibleNiños")
    val compatibleNinos: Boolean? = null,
    @SerializedName("compatibleOtrasMascotas")
    val compatibleOtrasMascotas: Boolean? = null, // <- FALTABA
    @SerializedName("estaVacunado")
    val estaVacunado: Boolean? = null,         // <- FALTABA
    @SerializedName("estaEsterilizado")
    val estaEsterilizado: Boolean? = null,     // <- FALTABA

    val historialMedico: String? = null,
    val fechaIngresoRefugio: String? = null,

    // IDs (Int?)
    val especieId: Int? = null,
    val razaId: Int? = null,
    val generoId: Int? = null,
    val tamanoId: Int? = null,
    val nivelEnergiaId: Int? = null,

    @SerializedName("estadoAdopcionId")
    val estadoAdopcionId: Int? = null,

    // Listas
    @SerializedName("temperamentosIds")
    val temperamentosIds: List<Int>? = emptyList(),

    @SerializedName("fotosUrls")
    val fotosUrls: List<String>? = emptyList(),

    @SerializedName("fotoPrincipalIndex")
    val fotoPrincipalIndex: Int? = null
)
