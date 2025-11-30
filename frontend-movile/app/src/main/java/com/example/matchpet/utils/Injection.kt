package com.example.matchpet.utils

import android.content.Context // 🔑 Importación necesaria
import com.example.matchpet.data.network.ApiService
import com.example.matchpet.data.repository.AnimalRepository
import com.example.matchpet.data.repository.RefugioRepository
import com.example.matchpet.viewmodel.MisMascotasViewModel
import com.example.matchpet.viewmodel.NuevaMascotaViewModel
import com.example.matchpet.viewmodel.refugio.RefugioViewModelFactory
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

    // 🔑 1. Variable para almacenar el Context de la aplicación
    private lateinit var appContext: Context

    // 🔑 2. Implementación de Repositorios con lazy para usar el Context
    private val animalRepository: AnimalRepository by lazy {
        AnimalRepository(apiService, appContext) // 👈 Aquí inyectamos el appContext
    }
    private val refugioRepository: RefugioRepository by lazy {
        RefugioRepository(apiService)
    }

    // 🔑 3. Función para inicializar el Context
    fun initialize(context: Context) {
        this.appContext = context.applicationContext
    }

    // --- Configuración Retrofit (Sin cambios) ---
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

    val apiService: ApiService = retrofit.create(ApiService::class.java)

    // --- Proveedores de Factory (Sin cambios) ---

    fun provideMisMascotasViewModelFactory(): MisMascotasViewModel.Factory {
        return MisMascotasViewModel.Factory(animalRepository)
    }

    // 🔑 PROVEEDOR AGREGADO para NuevaMascotaViewModel (Creación/Edición)
    fun provideNuevaMascotaViewModelFactory(): NuevaMascotaViewModel.Factory {
        return NuevaMascotaViewModel.Factory(animalRepository)
    }

    fun provideRefugioViewModelFactory(): RefugioViewModelFactory {
        return RefugioViewModelFactory(refugioRepository)
    }

    // 5. Constante para la URL base (para Coil)
    const val BACKEND_BASE_URL = BASE_URL
}