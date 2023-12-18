package com.example.voicebiometricsystem

import android.Manifest.permission
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaRecorder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.view.isInvisible
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.voicebiometricsystem.databinding.ActivityMainBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date


const val REQUEST_CODE = 200

class MainActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private var permissions: Array<String> = arrayOf(permission.RECORD_AUDIO)
    private var permissionGranted = false

    private var recorder: MediaRecorder? = null
    private var wavRecorder: WavRecorder? = null
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
//            return
        }

        timer = Timer(this)

        binding.loginButton.setOnClickListener {
            startRecording()
        }

        binding.stopButton.setOnClickListener {
            stopRecording()
        }

        binding.button.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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

        binding.loginButton.isInvisible = true
        binding.stopButton.isInvisible = false

        dirPath = "${externalCacheDir?.absolutePath}"

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")

        val date = simpleDateFormat.format(Date())
        filename = "audio_record_$date.wav"

        binding.waveformView.clearAmplitudes()

        wavRecorder = WavRecorder()

        wavRecorder?.startRecording(filename, dirPath)

        binding.loginButton.isEnabled = false
        isRecording = true

        timer.start()

    }

    private fun stopRecording() {
        timer.stop()

        binding.loginButton.isInvisible = false
        binding.stopButton.isInvisible = true

        binding.loginButton.isEnabled = true
        isRecording = false

        wavRecorder?.stopRecording()
    }

    @SuppressLint("SetTextI18n")
    override fun onTimerTick(duration: String) {

        wavRecorder?.getAmplitude()?.let { binding.waveformView.addAmplitude(it) }

        if (duration.toInt() == 3000) {
            stopRecording()

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }

            val python = Python.getInstance()
            val pythonFile = python.getModule("script")

            val trainFunc = pythonFile[ "read_audio_file" ]
            val result = trainFunc?.call( "$dirPath/$filename", "", "test")

            println(result.toString())
            binding.textView.setText("Recognized as: " + result.toString())
        }
    }
}