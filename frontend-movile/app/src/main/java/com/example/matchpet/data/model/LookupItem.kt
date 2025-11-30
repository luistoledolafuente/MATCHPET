package com.example.matchpet.data.model

import com.google.gson.annotations.SerializedName

/**
 * Modelo genérico para manejar datos de listas (Lookups) como Especies, Razas, Géneros, etc.
 * que típicamente tienen un ID y un nombre.
 *
 * NOTA: La estructura de la respuesta de la API puede variar:
 * - Razas: { "id": 1, "nombreRaza": "Labrador" }
 * - Especies: { "id": 1, "nombreEspecie": "Perro" }
 * - Temperamentos: { "id": 1, "nombreTemperamento": "Juguetón" }
 *
 * Para simplificar, usamos nombres genéricos y luego mapeamos la respuesta.
 */
data class LookupItem(
    val id: Int,
    val nombre: String
)

// Las clases de respuesta de la API para cada lookup deben coincidir con lo que devuelve el backend.
// Asumiremos las estructuras más probables basándonos en tu descripción de DB:

data class RazaResponse(
    @SerializedName("raza_id") val id: Int,
    @SerializedName("nombre_raza") val nombreRaza: String,
    @SerializedName("especie_id") val especieId: Int
)

data class EspecieResponse(
    @SerializedName("specie_id") val id: Int,
    @SerializedName("nombre_especie") val nombreEspecie: String
)

data class GeneroResponse(
    @SerializedName("genero_id") val id: Int,
    @SerializedName("nombre") val nombre: String
)

data class TemperamentoResponse(
    @SerializedName("temperamento_id") val id: Int,
    @SerializedName("nombre_temperamento") val nombreTemperamento: String
)

data class EstadoAdopcionResponse(
    @SerializedName("estado_adopcion_id") val id: Int,
    @SerializedName("nombre") val nombre: String
)


data class TamanoResponse(
    @SerializedName("tamano_id") val id: Int,
    @SerializedName ("nombre") val nombre: String
)
data class NivelEnergiaResponse(
    @SerializedName("nivel_energia_id") val id: Int,
    @SerializedName ("nombre") val nombre: String)
