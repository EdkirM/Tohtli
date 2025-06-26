package com.example.tohtli2

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AudioStorageHelper {
    private const val AUDIO_DIRECTORY = "tohtli_audios"
    private const val TRANSCRIPTION_SUFFIX = "_transcription.txt"

    // Eliminamos el formateador como propiedad estática
    private fun getDateFormatter(): SimpleDateFormat {
        return SimpleDateFormat("yyyy-MM-dd_HH-mm-ss", Locale.getDefault())
    }

    fun getAppAudioDirectory(context: Context): File {
        val directory = File(context.getExternalFilesDir(null), AUDIO_DIRECTORY)
        if (!directory.exists()) {
            directory.mkdirs()
        }
        return directory
    }

    fun createNewAudioFile(context: Context): File {
        val timestamp = getDateFormatter().format(Date()) // Usamos el formateador localmente
        val directory = getAppAudioDirectory(context)
        return File(directory, "audio_$timestamp.mp4")
    }

    fun getTranscriptionFile(audioFile: File): File {
        return File(audioFile.parent, "${audioFile.nameWithoutExtension}$TRANSCRIPTION_SUFFIX")
    }

    fun saveTranscription(audioFile: File, text: String) {
        val transcriptionFile = getTranscriptionFile(audioFile)
        transcriptionFile.writeText(text)
    }

    fun getAllAudioFiles(context: Context): List<File> {
        val directory = getAppAudioDirectory(context)
        return directory.listFiles()
            ?.filter { it.extension == "mp4" }
            ?.sortedByDescending { it.lastModified() }
            ?: emptyList()
    }

    fun getTranscriptionIfExists(audioFile: File): String? {
        val transcriptionFile = getTranscriptionFile(audioFile)
        return if (transcriptionFile.exists()) {
            transcriptionFile.readText()
        } else {
            null
        }
    }
}