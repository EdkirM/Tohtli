package com.example.tohtli2

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

fun createWhisperService(): WhisperApiService {
    val client = OkHttpClient.Builder()
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "Bearer aqui va la APIPIPIP") // ⚠️ reemplaza con tu clave
                .build()
            chain.proceed(request)
        }
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        })
        .build()

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.openai.com/")
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    return retrofit.create(WhisperApiService::class.java)
}
