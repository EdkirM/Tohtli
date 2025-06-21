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
