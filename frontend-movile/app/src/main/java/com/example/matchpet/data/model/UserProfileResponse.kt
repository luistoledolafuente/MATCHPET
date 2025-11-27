package com.example.matchpet.data.model

import com.google.gson.annotations.SerializedName

data class UserProfileResponse(
    val usuarioId: Int,
    val email: String,
    val nombre: String,
    val apellidoPaterno: String,
    val apellidoMaterno: String,
    val telefono: String?,
    val fechaNacimiento: String,
    val direccion: String?,
    val ciudad: String?,
    val pais: String?,
    val roles: List<String>,

    // ⭐ ESTE ES EL CAMPO QUE TE FALTABA
    @SerializedName("refugio")
    val refugio: RefugioProfileResponse?
)
