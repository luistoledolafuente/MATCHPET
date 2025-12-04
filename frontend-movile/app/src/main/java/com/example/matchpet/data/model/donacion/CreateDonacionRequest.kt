package com.example.matchpet.data.model.donacion

import java.math.BigDecimal

data class CreateDonacionRequest(
    val monto: BigDecimal,
    val moneda: String = "PEN",
    val nombreDonante: String? = null,
    val emailDonante: String? = null,
    val refugioId: Int? = null,
    val animalId: Int? = null,
    val mensajeDonante: String? = null,
    val successUrl: String? = null,
    val failureUrl: String? = null
)
