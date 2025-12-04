package com.example.matchpet.data.model.donacion

data class DonacionResponse(
    val id: Int,
    val monto: Double,
    val moneda: String,
    val estado: String,
    val fechaDonacion: String?,
    val nombreDonante: String?,
    val emailDonante: String?,
    val refugioNombre: String?,
    val animalNombre: String?,
    val mensajeDonante: String?
)
