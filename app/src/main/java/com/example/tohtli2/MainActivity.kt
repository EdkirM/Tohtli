package com.example.tohtli2

////////////////////////////////77
import android.widget.TextView
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MultipartBody
import java.io.File

////////////////////////////////



import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.example.tohtli2.databinding.ActivityMainBinding
import com.google.android.material.bottomnavigation.BottomNavigationView
import java.io.*

import com.example.tohtli2.WhisperApiService
import com.example.tohtli2.WhisperResponse
import com.example.tohtli2.createWhisperService



class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var isRecording = false
    private lateinit var audioThread: Thread

    private val sampleRate = 44100
    private val audioSource = MediaRecorder.AudioSource.MIC
    private val channelConfig = AudioFormat.CHANNEL_IN_MONO
    private val audioFormat = AudioFormat.ENCODING_PCM_16BIT
    private val bufferSize = AudioRecord.getMinBufferSize(sampleRate, channelConfig, audioFormat)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val navView: BottomNavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE), 0)
        }

        // Botón de grabación
        val recordButton: Button = findViewById(R.id.recordButton)
        recordButton.setOnClickListener {
            if (!isRecording) {
                startRecording(recordButton)
            } else {
                stopRecording(recordButton)
            }
        }


        /////////////////////////////////////////////////////////////////////
        ////////////////////////////////////////////////////////////////////
        val transcribeButton: Button = findViewById(R.id.transcribeButton)
        val transcriptionText: TextView = findViewById(R.id.transcriptionText)

        transcribeButton.setOnClickListener {
            val audioFile = File(getExternalFilesDir(null), "audio.wav")
            if (audioFile.exists()) {
                transcribirAudio(audioFile, transcriptionText)
            } else {
                Toast.makeText(this, "No se encontró el archivo .wav", Toast.LENGTH_SHORT).show()
            }
        }

        /////////////////////////////////////////////////////////////////////
        /////////////////////////////////////////////////////////////////////
    }

////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////

    private fun transcribirAudio(file: File, textView: TextView) {
        val service = createWhisperService()

        val requestFile = file.asRequestBody("audio/wav".toMediaTypeOrNull())
        val body = MultipartBody.Part.createFormData("file", file.name, requestFile)
        val model = "whisper-1".toRequestBody("text/plain".toMediaTypeOrNull())

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = service.transcribeAudio(body, model)
                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val transcription = response.body()?.text ?: "Sin texto"
                        textView.text = transcription
                    } else {
                        textView.text = "Error: ${response.code()}"
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    textView.text = "Fallo: ${e.localizedMessage}"
                }
            }
        }
    }

////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////


    @SuppressLint("MissingPermission")
    private fun startRecording(recordButton: Button) {
        isRecording = true
        recordButton.text = "Detener Grabación"

        audioThread = Thread {
            val audioRecord = AudioRecord(audioSource, sampleRate, channelConfig, audioFormat, bufferSize)
            val pcmFile = File(getExternalFilesDir(null), "temp_audio.pcm")
            val wavFile = File(getExternalFilesDir(null), "audio.wav")
            val data = ByteArray(bufferSize)

            audioRecord.startRecording()
            FileOutputStream(pcmFile).use { os ->
                while (isRecording) {
                    val read = audioRecord.read(data, 0, data.size)
                    if (read > 0) os.write(data, 0, read)
                }
            }

            audioRecord.stop()
            audioRecord.release()

            rawToWave(pcmFile, wavFile)
            pcmFile.delete()

            runOnUiThread {
                Toast.makeText(this, "Audio guardado como WAV", Toast.LENGTH_SHORT).show()
            }
        }

        audioThread.start()
    }

    private fun stopRecording(recordButton: Button) {
        isRecording = false
        recordButton.text = "Grabar Audio"
    }

    private fun rawToWave(rawFile: File, waveFile: File) {
        val rawData = rawFile.readBytes()
        val totalAudioLen = rawData.size
        val totalDataLen = totalAudioLen + 36
        val byteRate = 16 * sampleRate * 1 / 8

        FileOutputStream(waveFile).use { out ->
            val header = ByteArray(44)
            header[0] = 'R'.code.toByte(); header[1] = 'I'.code.toByte(); header[2] = 'F'.code.toByte(); header[3] = 'F'.code.toByte()
            writeInt(header, 4, totalDataLen)
            header[8] = 'W'.code.toByte(); header[9] = 'A'.code.toByte(); header[10] = 'V'.code.toByte(); header[11] = 'E'.code.toByte()
            header[12] = 'f'.code.toByte(); header[13] = 'm'.code.toByte(); header[14] = 't'.code.toByte(); header[15] = ' '.code.toByte()
            writeInt(header, 16, 16)
            writeShort(header, 20, 1.toShort())
            writeShort(header, 22, 1.toShort())
            writeInt(header, 24, sampleRate)
            writeInt(header, 28, byteRate)
            writeShort(header, 32, 2.toShort())
            writeShort(header, 34, 16.toShort())
            header[36] = 'd'.code.toByte(); header[37] = 'a'.code.toByte(); header[38] = 't'.code.toByte(); header[39] = 'a'.code.toByte()
            writeInt(header, 40, totalAudioLen)

            out.write(header, 0, 44)
            out.write(rawData)
        }
    }

    private fun writeInt(header: ByteArray, offset: Int, value: Int) {
        header[offset] = (value and 0xff).toByte()
        header[offset + 1] = ((value shr 8) and 0xff).toByte()
        header[offset + 2] = ((value shr 16) and 0xff).toByte()
        header[offset + 3] = ((value shr 24) and 0xff).toByte()
    }

    private fun writeShort(header: ByteArray, offset: Int, value: Short) {
        header[offset] = (value.toInt() and 0xff).toByte()
        header[offset + 1] = ((value.toInt() shr 8) and 0xff).toByte()
    }
}
