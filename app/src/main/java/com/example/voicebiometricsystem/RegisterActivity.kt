package com.example.voicebiometricsystem

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isInvisible
import com.example.voicebiometricsystem.databinding.RegisterLayoutBinding
import java.text.SimpleDateFormat
import java.util.Date
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform


class RegisterActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private lateinit var binding: RegisterLayoutBinding
    private lateinit var timer: Timer
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var wavRecorder: WavRecorder? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timer = Timer(this)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.stopButton.setOnClickListener {
            stopRecording()
        }

        binding.registerButton.setOnClickListener {
            if (binding.textInput.text.toString() == "") {
                Toast.makeText(this, "Name is required ", Toast.LENGTH_SHORT).show()
            } else {
                startRecording()
            }
        }
    }

    private fun startRecording() {

        binding.registerButton.isInvisible = true
        binding.stopButton.isInvisible = false

        dirPath = "${externalCacheDir?.absolutePath}"

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")

        val date = simpleDateFormat.format(Date())
        filename = "audio_record_$date.wav"

        binding.waveformView.clearAmplitudes()

        wavRecorder = WavRecorder()

        wavRecorder?.startRecording(filename, dirPath)

        binding.registerButton.isEnabled = false
        isRecording = true

        timer.start()

    }

    private fun stopRecording() {
        timer.stop()

        binding.registerButton.isInvisible = false
        binding.stopButton.isInvisible = true

        binding.registerButton.isEnabled = true
        isRecording = false

        wavRecorder?.stopRecording()
    }

    override fun onTimerTick(duration: String) {

        wavRecorder?.getAmplitude()?.let { binding.waveformView.addAmplitude(it) }

        if (duration.toInt() == 20000) {
            stopRecording()

            if (!Python.isStarted()) {
                Python.start(AndroidPlatform(this))
            }

            val python = Python.getInstance()
            val pythonFile = python.getModule("script")

            val trainFunc = pythonFile[ "read_audio_file" ]
            val result = trainFunc?.call( "$dirPath/$filename", binding.textInput.text.toString(), "train")

            println(result.toString())

            Toast.makeText(this, "Registered successfully", Toast.LENGTH_SHORT).show()
        }

    }

}