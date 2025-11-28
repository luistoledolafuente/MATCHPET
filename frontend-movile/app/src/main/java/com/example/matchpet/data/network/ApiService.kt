package com.example.matchpet.data.network

import com.example.matchpet.data.model.*
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ---------------- AUTENTICACIÓN Y REGISTRO --------------------------------
    @POST("/api/adoptantes/register")
    suspend fun registerAdoptante(@Body request: AdopterRegisterRequest): Response<AuthResponse>

    @POST("/api/refugios/register")
    suspend fun registerRefugio(@Body request: ShelterRegisterRequest): Response<AuthResponse>

    @POST("/api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>


    // ------------- PERFIL DE USUARIO -----------------------------------
    @PUT("/api/refugios/{id}")
    suspend fun updateRefugioProfile(
        @Path("id") id: Int,
        @Header("Authorization") token: String,
        @Body request: RefugioUpdateRequest
    ): Response<RefugioProfileResponse>

    @GET("/api/user/profile")
    suspend fun getProfile(@Header("Authorization") token: String): Response<UserProfileResponse>

    @PUT("/api/adoptantes/{id}/profile")
    suspend fun updateAdoptanteProfile(
        @Header("Authorization") token: String,
        @Path("id") id: Int,
        @Body request: UpdateAdoptanteRequest
    ): Response<UserProfileResponse>


    // ---------------END POINTS DE ANIMALES ------------------------------------

    @GET("/api/animales")
    suspend fun getAnimales(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageResponse<Animal>>

    @GET("/api/animales/{id}")
    suspend fun getAnimalDetails(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Response<AnimalResponse>

    @GET("api/animales/mis-animales")
    suspend fun getMisAnimales(@Header("Authorization") token: String): Response<List<AnimalResponse>>

    @POST("/api/animales")
    suspend fun createAnimal(
        @Header("Authorization") token: String, @Body request: AnimalCreationRequest
    ): Response<AnimalResponse>

    @PUT("/api/animales/{id}")
    suspend fun updateAnimal(
        @Path("id") id: Int, @Header("Authorization") token: String, @Body request: AnimalUpdateRequest
    ): Response<AnimalResponse>

    @DELETE("/api/animales/{id}")
    suspend fun deleteAnimal(@Path("id") id: String, @Header("Authorization") token: String): Response<Unit>

    @Multipart
    @POST("/api/animales/upload")
    suspend fun uploadImage(@Part file: MultipartBody.Part, @Header("Authorization") token: String): Response<ImageUploadResponse>

    // --------------- SOLICITUDES ------------------------------------
    @POST("/api/solicitudes")
    suspend fun createSolicitud(
        @Header("Authorization") token: String,
        @Body request: SolicitudRequest
    ): Response<Void>
}