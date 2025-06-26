package com.example.tohtli2

// Importaciones necesarias para construir el cliente Retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Función que crea e inicializa el servicio de Whisper con Retrofit
fun createWhisperService(): WhisperApiService {
    // Se crea un cliente HTTP personalizado con OkHttp
    val client = OkHttpClient.Builder()

        // Interceptor para agregar el encabezado de autorización (API Key de OpenAI)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer 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") // ⚠️ Reemplaza con tu clave real
                //.addHeader("Authorization", "Bearer ${BuildConfig.OPENAI_API_KEY}")
                .build()
            chain.proceed(request)
        }

        // Interceptor para mostrar los logs de las solicitudes/respuestas HTTP (muy útil para depuración)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Muestra todo el cuerpo de las peticiones/respuestas
        })

        .build()

    // Se construye la instancia de Retrofit
    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/") // URL base de la API de OpenAI
        .client(client) // Cliente HTTP configurado arriba
        .addConverterFactory(GsonConverterFactory.create()) // Conversor de JSON a objetos Kotlin
        .build()

    // Devuelve una implementación de la interfaz WhisperApiService
    return retrofit.create(WhisperApiService::class.java)
}
