package com.example.matchpet.data.repository

/**
 * Clase sellada (sealed class) para manejar los estados de las operaciones del repositorio
 * (Loading, Success, Error) y asegurar la compilación de AnimalRepository.
 */
sealed class Resource<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T) : Resource<T>(data)
    class Error<T>(message: String, data: T? = null) : Resource<T>(data, message)
    class Loading<T>(data: T? = null) : Resource<T>(data)
}