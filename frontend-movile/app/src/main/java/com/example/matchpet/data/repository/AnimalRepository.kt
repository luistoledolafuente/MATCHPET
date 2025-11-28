package com.example.matchpet.data.repository

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import com.example.matchpet.data.model.AnimalCreationRequest
import com.example.matchpet.data.model.AnimalResponse
import com.example.matchpet.data.model.AnimalUpdateRequest
import com.example.matchpet.data.model.ImageUploadResponse
import com.example.matchpet.data.network.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.IOException

// 🔑 CAMBIO CLAVE: El contexto (Context) es necesario para leer los archivos de la Uri.
class AnimalRepository(
    private val apiService: ApiService,
    private val appContext: Context
) {

    // 1. OBTENER: Lista de animales
    fun getMisAnimales(token: String): Flow<Resource<List<AnimalResponse>>> = flow {
        emit(Resource.Loading())
        try {
            val response = apiService.getMisAnimales("Bearer $token")
            if (response.isSuccessful) {
                val animales = response.body() ?: emptyList()
                emit(Resource.Success(animales))
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                emit(Resource.Error("Error ${response.code()}: $errorBody"))
            }
        } catch (e: Exception) {
            emit(Resource.Error("Fallo en la red o conversión de datos: ${e.localizedMessage}"))
        }
    }

    // 2. OBTENER: Detalles del animal
    suspend fun getAnimalDetails(token: String, animalId: Int): Resource<AnimalResponse> {
        return try {
            val response = apiService.getAnimalDetails(animalId, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Resource.Error("Fallo al obtener detalles: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red al obtener detalles: ${e.localizedMessage}")
        }
    }

    // 3. POST: Registrar un nuevo animal
    suspend fun createAnimal(
        token: String,
        request: AnimalCreationRequest
    ): Resource<AnimalResponse> {
        return try {
            val response = apiService.createAnimal("Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Resource.Error("Fallo al registrar animal: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red al registrar animal: ${e.localizedMessage}")
        }
    }

    // 4. PUT: Actualizar un animal
    suspend fun updateAnimal(
        id: Int,
        token: String,
        request: AnimalUpdateRequest
    ): Resource<AnimalResponse> {
        return try {
            val response = apiService.updateAnimal(id, "Bearer $token", request)
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Resource.Error("Fallo al actualizar animal: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red al actualizar animal: ${e.localizedMessage}")
        }
    }

    // 5. DELETE: Eliminar un animal
    suspend fun deleteAnimal(token: String, animalId: String): Resource<Unit> {
        return try {
            val response = apiService.deleteAnimal("Bearer $token", animalId)
            if (response.isSuccessful) {
                Resource.Success(Unit)
            } else {
                val errorBody = response.errorBody()?.string() ?: response.message()
                Resource.Error("Error al eliminar mascota: $errorBody")
            }
        } catch (e: IOException) {
            Resource.Error("Fallo de red. Verifica tu conexión o el servidor.")
        } catch (e: Exception) {
            Resource.Error("Ocurrió un error inesperado al eliminar: ${e.message}")
        }
    }

    // 6. POST: Subir imagen (Recibe Uri del ViewModel y llama a la API)
    suspend fun uploadImage(token: String, fileUri: Uri): Resource<ImageUploadResponse> = withContext(Dispatchers.IO) {
        // 1. Convertir Uri a MultipartBody.Part
        val imagePart = try {
            createPartFromUri(fileUri)
        } catch (e: Exception) {
            return@withContext Resource.Error("Error al preparar la imagen para subir: ${e.localizedMessage}")
        }

        // 2. Llamar al servicio con el MultipartBody.Part
        return@withContext try {
            val response = apiService.uploadImage(imagePart, "Bearer $token")
            if (response.isSuccessful && response.body() != null) {
                Resource.Success(response.body()!!)
            } else {
                val errorBody = response.errorBody()?.string() ?: "Error desconocido"
                Resource.Error("Fallo al subir imagen: ${response.code()} - $errorBody")
            }
        } catch (e: Exception) {
            Resource.Error("Error de red al subir imagen: ${e.localizedMessage}")
        }
    }

    // 🔑 FUNCIÓN DE AYUDA para convertir Uri a MultipartBody.Part
    private fun createPartFromUri(uri: Uri): MultipartBody.Part {
        val contentResolver = appContext.contentResolver
        val inputStream = contentResolver.openInputStream(uri)
            ?: throw IOException("No se pudo abrir el InputStream para la Uri: $uri")

        val mimeType = contentResolver.getType(uri) ?: "application/octet-stream"

        // Obtenemos el nombre del archivo si es posible
        val fileName = getFileName(uri)

        // Leemos el contenido del InputStream a un ByteArray
        val fileBytes = inputStream.readBytes()
        inputStream.close()

        // Creamos el RequestBody
        val requestBody = fileBytes.toRequestBody(mimeType.toMediaTypeOrNull())

        // Creamos el MultipartBody.Part. Asumo que tu backend espera el campo "file"
        return MultipartBody.Part.createFormData("file", fileName, requestBody)
    }

    // 🔑 FUNCIÓN DE AYUDA para obtener el nombre de archivo de una Uri
    private fun getFileName(uri: Uri): String {
        var name = "uploaded_file"
        if (uri.scheme == "content") {
            appContext.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                if (cursor.moveToFirst()) {
                    val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                    if (nameIndex != -1) {
                        name = cursor.getString(nameIndex)
                    }
                }
            }
        } else if (uri.path != null) {
            name = uri.pathSegments.last()
        }
        return name
    }
}