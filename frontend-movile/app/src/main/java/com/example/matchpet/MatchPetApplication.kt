package com.example.matchpet

import android.app.Application
import com.example.matchpet.utils.Injection

/**
 * Clase Application personalizada para inicializar el sistema de Inyección de Dependencias.
 * Esta versión mínima evita los errores de compilación con BuildConfig/Timber.
 */
class MatchPetApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Injection.initialize(applicationContext)
    }
}