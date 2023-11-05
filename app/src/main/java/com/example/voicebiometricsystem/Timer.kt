package com.example.voicebiometricsystem

import android.os.Handler
import android.os.Looper

class Timer(listener: OnTimerTickListener) {

    interface OnTimerTickListener {
        fun onTimerTick(duration:String)
    }

    private var handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    private var duration = 0L
    private var delay = 100L

    init {
        runnable = Runnable {
            duration += delay
            handler.postDelayed(runnable, delay)

            listener.onTimerTick(duration.toString())

        }
    }

    fun start() {
        handler.postDelayed(runnable, delay)
    }

    fun stop() {
        handler.removeCallbacks(runnable)
        duration = 0L
    }
}