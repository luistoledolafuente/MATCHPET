package com.example.matchpet

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.example.matchpet.utils.Injection
import okhttp3.OkHttpClient

/**
 * Clase Application personalizada para inicializar el sistema de Inyección de Dependencias
 * y configurar Coil para carga de imágenes.
 */
class MatchPetApplication : Application(), ImageLoaderFactory {

    override fun onCreate() {
        super.onCreate()
        Injection.initialize(applicationContext)
    }

    override fun newImageLoader(): ImageLoader {
        return ImageLoader.Builder(this)
            .crossfade(true)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .memoryCache {
                MemoryCache.Builder(this)
                    .maxSizePercent(0.25)
                    .build()
            }
            .diskCachePolicy(CachePolicy.ENABLED)
            .diskCache {
                DiskCache.Builder()
                    .directory(cacheDir.resolve("image_cache"))
                    .maxSizeBytes(50 * 1024 * 1024) // 50MB
                    .build()
            }
            .okHttpClient {
                OkHttpClient.Builder()
                    .build()
            }
            .logger(DebugLogger())
            .build()
    }
}