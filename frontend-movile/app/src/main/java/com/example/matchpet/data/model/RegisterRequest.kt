package com.example.matchpet.data.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val nombreCompleto: String,
    val telefono: String
)
