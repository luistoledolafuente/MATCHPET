package com.example.matchpet.utils

import com.example.matchpet.data.network.ApiService
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.RefugioRepository
import com.example.matchpet.viewmodel.MisMascotasViewModel
import com.example.matchpet.viewmodel.RefugioViewModelFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

// 🔥 URL segura para emuladores
private const val BASE_URL = "http://10.0.2.2:8081"

/**
 * Provee instancias de la API, repositorios y ViewModels de forma centralizada.
 */
object Injection {

    // 1. Configuración del Cliente HTTP
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(20, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .writeTimeout(20, TimeUnit.SECONDS)
        .addInterceptor(loggingInterceptor)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    // 2. Servicio Retrofit accesible
    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // 3. Repositorios
    private val animalRepository: AnimalRepository = AnimalRepository(apiService)
    private val refugioRepository: RefugioRepository = RefugioRepository(apiService)

    // 4. Proveedores de Factory

    // ✅ CORRECCIÓN: Referencia directa a MisMascotasViewModel.Factory (sin .Companion)
    fun provideMisMascotasViewModelFactory(): MisMascotasViewModel.Factory {
        return MisMascotasViewModel.Factory(animalRepository)
    }

    fun provideRefugioViewModelFactory(): RefugioViewModelFactory {
        return RefugioViewModelFactory(refugioRepository)
    }

    // 5. Constante para la URL base (para Coil)
    const val BACKEND_BASE_URL = BASE_URL
}