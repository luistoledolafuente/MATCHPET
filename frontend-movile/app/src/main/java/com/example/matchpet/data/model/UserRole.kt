package com.example.matchpet.data.model

enum class UserRole(val displayName: String) {
    ADOPTER("Adoptante"),
    SHELTER("Refugio");

    companion object {
        fun fromString(value: String): UserRole {
            return when (value.uppercase()) {
                "ADOPTER", "ADOPTANTE" -> ADOPTER
                "SHELTER", "REFUGIO" -> SHELTER
                else -> throw IllegalArgumentException("Unknown role: $value")
            }
        }
    }
}