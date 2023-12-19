package com.example.voicebiometricsystem

import android.media.MediaRecorder
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.voicebiometricsystem.databinding.RegisterLayoutBinding
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date

class RegisterActivity : AppCompatActivity(), Timer.OnTimerTickListener {

    private lateinit var binding: RegisterLayoutBinding
    private lateinit var timer: Timer
    private var dirPath = ""
    private var filename = ""
    private var isRecording = false
    private var recorder: MediaRecorder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegisterLayoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        timer = Timer(this)

        binding.registerButton.setOnClickListener {
            startRecording()
        }
    }

    private fun startRecording() {
        dirPath = "${externalCacheDir?.absolutePath}/"

        val simpleDateFormat = SimpleDateFormat("yyyy.MM.DD_hh.mm.ss")

        val date = simpleDateFormat.format(Date())
        filename = "audio_record_$date"

        recorder = MediaRecorder(this).apply {
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
        binding.registerButton.isEnabled = false
        isRecording = true

        timer.start()

    }

    private fun stopRecording() {
        timer.stop()
        binding.registerButton.isEnabled = true
        isRecording = false
    }

    override fun onTimerTick(duration: String) {

        recorder?.maxAmplitude?.let { binding.waveformView.addAmplitude(it.toFloat()) }
        recorder?.maxAmplitude?.let { println(it.toFloat()) }

        if (duration.toInt() == 20000) {
            stopRecording()
        }
    }


}