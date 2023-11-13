package com.example.voicebiometricsystem

import android.Manifest.permission
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.example.voicebiometricsystem.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

const val REQUEST_CODE = 200

class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private var permissions: Array<String> = arrayOf(permission.RECORD_AUDIO)
    private var permissionGranted = false

    private lateinit var recorder: MediaRecorder
    private lateinit var binding: ActivityMainBinding

    private var dirPath = ""
    private var filename = ""
    private var isRecording = false

    private lateinit var timer: Timer

//    private lateinit var context: Context

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        permissionGranted = ActivityCompat.checkSelfPermission(this, permissions[0]) == PackageManager.PERMISSION_GRANTED

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
        }

        timer = Timer(this)

        binding.loginButton.setOnClickListener {
            startRecording()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == REQUEST_CODE) {
            permissionGranted = grantResults[0] == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun startRecording() {

        if (!permissionGranted) {
            ActivityCompat.requestPermissions(this, permissions, REQUEST_CODE)
            return
        }

        recorder = MediaRecorder(this)
        dirPath = "{$externalCacheDir?.absolutePath}/"

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")

        val date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder.apply {
            setAudioSource(MediaRecorder.AudioSource.MIC)
            setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
            setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
            setOutputFile("$dirPath$filename.wav")

            try {
                prepare()
                start()
            }catch (e: IOException){
                println(e)
            }
        }
        binding.loginButton.isEnabled = false
        isRecording = true

        timer.start()

    }

    private fun stopRecording() {
        timer.stop()
        binding.loginButton.isEnabled = true
        isRecording = false
    }

    override fun onTimerTick(duration: String) {

        binding.waveformView.addAmplitude(recorder.maxAmplitude.toFloat())
        println(recorder.maxAmplitude.toFloat())

        if (duration.toInt() == 5000) {
            stopRecording()
        }


    }
}