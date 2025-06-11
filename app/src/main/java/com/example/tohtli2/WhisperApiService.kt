package com.example.tohtli2

// Importaciones necesarias para manejar solicitudes HTTP con Retrofit
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

// Interfaz que define el servicio para consumir la API de Whisper usando Retrofit
interface WhisperApiService {

    // Anotación para indicar que se trata de una solicitud multipart/form-data
    @Multipart
    @POST("v1/audio/transcriptions") // Ruta del endpoint de transcripción
    suspend fun transcribeAudio(
        // Parte del archivo de audio, enviado como multipart
        @Part file: MultipartBody.Part,
        // Parte que indica el modelo a usar (en este caso "whisper-1")
        @Part("model") model: RequestBody
    ): Response<WhisperResponse> // Devuelve una respuesta Retrofit con un objeto WhisperResponse
}

// Clase de datos que representa la respuesta JSON de la API de Whisper
// Se espera que tenga un campo "text" con la transcripción
data class WhisperResponse(val text: String)
