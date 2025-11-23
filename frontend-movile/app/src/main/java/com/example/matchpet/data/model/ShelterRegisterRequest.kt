package com.example.matchpet.data.model

data class ShelterRegisterRequest(
    val emailLogin: String, // Endpoint usa emailLogin
    val password: String,
    val nombreRefugio: String,
    val descripcion: String,
    val direccion: String,
    val ciudad: String,
    val pais: String,
    val emailRefugio: String, // Email del refugio (contacto)
    val personaContacto: String,
    val telefonoContacto: String,
    val urlSitioWeb: String? = null // Hacer opcional
)