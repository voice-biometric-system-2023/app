package com.example.voicebiometricsystem

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View

class WaveformView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {

    private var paint = Paint()
    private var amplitudes = ArrayList<Float>()
    private var spikes = ArrayList<RectF>()

    private var radius = 6f
    private var w = 9f
    private var d = 6f

    private var screenWidth = 0f
    private var screenHeight = 400f

    init {
        paint.color = Color.rgb(244, 81, 30)

        screenWidth = resources.displayMetrics.widthPixels.toFloat()
    }

    fun addAmplitude(amplitude: Float) {
        amplitudes.add(amplitude)

        spikes.clear()
        for (i in amplitudes.indices) {
            var left = screenWidth - i * (w + d)
            var top = 0f
            var right = left + w
            var bottom = amplitudes[i]


            spikes.add(RectF(left, top, right, bottom))
        }

        invalidate()
    }

    override fun draw(canvas: Canvas?) {
        super.draw(canvas)

        spikes.forEach {
            canvas?.drawRoundRect(it, radius, radius, paint)
        }
    }
}