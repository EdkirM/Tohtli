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
