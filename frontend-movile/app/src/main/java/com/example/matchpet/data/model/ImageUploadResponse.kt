package com.example.matchpet.data.model

import kotlinx.serialization.Serializable

/**
 * Representa la respuesta del servidor después de una subida exitosa de una imagen.
 *
 * @param url La URL pública donde se almacena la imagen subida.
 */
@Serializable
data class ImageUploadResponse(
    val url: String
)