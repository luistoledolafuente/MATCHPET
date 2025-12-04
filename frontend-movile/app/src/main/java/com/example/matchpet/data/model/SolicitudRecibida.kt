package com.example.matchpet.data.model

import com.google.gson.annotations.SerializedName

/**
 * ======== ADOPTANTE ========
 * El backend devuelve:
 *
 * "adoptante": {
 *   "usuarioId": 0,
 *   "email": "string",
 *   "nombreCompleto": "string",
 *   "telefono": "string",
 *   "perfil": {
 *     "id": 0,
 *     "fechaNacimiento": "2025-11-30",
 *     "direccion": "string",
 *     "ciudad": "string",
 *     "pais": "string"
 *   }
 * }
 */

data class AdoptanteSummary(
    @SerializedName("usuarioId") val id: Int,
    @SerializedName("email") val email: String,
    @SerializedName("nombreCompleto") val nombreCompleto: String,
    @SerializedName("telefono") val telefono: String?,
    @SerializedName("perfil") val perfil: PerfilAdoptante?
)

data class PerfilAdoptante(
    @SerializedName("id") val id: Int,
    @SerializedName("fechaNacimiento") val fechaNacimiento: String?,
    @SerializedName("direccion") val direccion: String?,
    @SerializedName("ciudad") val ciudad: String?,
    @SerializedName("pais") val pais: String?
)



/**
 * ======== ANIMAL ========
 * El backend devuelve:
 *
 * "animal": {
 *   "animal_id": 0,
 *   "nombre": "string",
 *   "fotos": ["string"]
 * }
 */

data class AnimalSummary(
    @SerializedName("animal_id") val id: Int,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("fotos") val fotos: List<String>?
)



/**
 * ======== ESTADO SOLICITUD ========
 * "estadoSolicitud": {
 *   "id": 0,
 *   "nombre": "string"
 * }
 */

data class EstadoSolicitudDto(
    @SerializedName("id") val id: Int,
    @SerializedName("nombre") val nombre: String
)



/**
 * ======== RESPUESTA DE SOLICITUD ========
 *
 * Estructura real del backend:
 *
 * {
 *   "id": 0,
 *   "fechaSolicitud": "2025-11-30T09:21:50.200Z",
 *   "fechaActualizacion": "2025-11-30T09:21:50.200Z",
 *   "notasInternas": "string",
 *   "mensajeAlAdoptante": "string",
 *   "estadoSolicitud": { ... },
 *   "animal": { ... },
 *   "adoptante": { ... }
 * }
 */

data class SolicitudResponse(
    @SerializedName("id") val id: Int,
    @SerializedName("fechaSolicitud") val fechaSolicitud: String,
    @SerializedName("fechaActualizacion") val fechaActualizacion: String?,
    @SerializedName("notasInternas") val notasInternas: String?,
    @SerializedName("mensajeAdoptante") val mensajeAdoptante: String?,
    @SerializedName("mensajeAlAdoptante") val mensajeAlAdoptante: String?,
    @SerializedName("estadoSolicitud") val estadoSolicitud: EstadoSolicitudDto,
    @SerializedName("animal") val animal: AnimalSummary,
    @SerializedName("adoptante") val adoptante: AdoptanteSummary
)



/**
 * ======== REQUEST PARA ACTUALIZAR ESTADO ========
 *
 * Backend espera:
 *
 * {
 *   "estadoSolicitudId": 0,
 *   "notasInternas": "string",
 *   "mensajeAlAdoptante": "string"
 * }
 */

data class SolicitudUpdateRequest(
    @SerializedName("estadoSolicitudId") val estadoSolicitudId: Int,
    @SerializedName("notasInternas") val notasInternas: String?,
    @SerializedName("mensajeAlAdoptante") val mensajeAlAdoptante: String?
)
