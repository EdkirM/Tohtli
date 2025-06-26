package com.example.tohtli2

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface WhisperApiService {
    @Multipart
    @POST("v1/audio/transcriptions")
    suspend fun transcribeAudio(
        @Part file: MultipartBody.Part,
        @Part("model") model: RequestBody,
        @Part("language") language: RequestBody? = null
    ): Response<WhisperResponse>

    @POST("v1/chat/completions")
    suspend fun translateText(
        @Body request: TranslationRequest
    ): Response<TranslationResponse>
}

data class WhisperResponse(
    val text: String,
    val language: String? = null
)

data class TranslationRequest(
    val model: String = "gpt-3.5-turbo",
    val messages: List<Message>,
    val temperature: Double = 0.7
)

data class Message(
    val role: String = "user",
    val content: String
)

data class TranslationResponse(
    val choices: List<Choice>
)

data class Choice(
    val message: Message
)