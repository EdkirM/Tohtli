package com.example.tohtli2

import android.content.Context
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object AudioStorageHelper {
    private const val AUDIO_DIRECTORY = "tohtli_audios"
    private const val TRANSCRIPTION_SUFFIX = "_transcription.txt"
    private const val TRANSLATION_PREFIX = "translation_"

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
        val timestamp = getDateFormatter().format(Date())
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

    fun saveTranslation(audioFile: File, languageCode: String, text: String) {
        val translationFile = getTranslationFile(audioFile, languageCode)
        translationFile.writeText(text)
    }

    fun getTranslationFile(audioFile: File, languageCode: String): File {
        return File(
            audioFile.parent,
            "${audioFile.nameWithoutExtension}_${TRANSLATION_PREFIX}$languageCode.txt"
        )
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

    fun getTranslationIfExists(audioFile: File, languageCode: String): String? {
        val translationFile = getTranslationFile(audioFile, languageCode)
        return if (translationFile.exists()) {
            translationFile.readText()
        } else {
            null
        }
    }

    fun getAllTranslations(audioFile: File): Map<String, String> {
        val directory = audioFile.parentFile
        val prefix = "${audioFile.nameWithoutExtension}_${TRANSLATION_PREFIX}"

        return directory.listFiles()
            ?.filter { it.name.startsWith(prefix) }
            ?.associate {
                val langCode = it.name
                    .removePrefix(prefix)
                    .removeSuffix(".txt")
                langCode to it.readText()
            }
            ?: emptyMap()
    }
}