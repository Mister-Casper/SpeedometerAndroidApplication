package com.sgc.speedometer.ui.customView.speedometer.render

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import com.sgc.speedometer.R
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

class TextSpeedometerRender(private val context: Context) : SpeedometerRender() {

    var speedPaint = Paint()
    var textPaint = Paint()
    var topTextPadding = 75

    init {
        speedPaint.textSize = 300.0f
        speedPaint.textAlign = Paint.Align.CENTER;
        speedPaint.color = context.getColor(R.color.text_color)
        textPaint.textSize = 100.0f
        textPaint.textAlign = Paint.Align.CENTER;
        textPaint.color = context.getColor(R.color.text_color)
    }

    override fun draw(canvas: Canvas, speed: Int, maxSpeed: Int, speedUnit: SpeedUnit, gpsEnable: Boolean) {
        drawSpeed(canvas, speed, gpsEnable)
        if (gpsEnable) {
            drawText(canvas, speedUnit)
        }
    }

    private fun drawSpeed(canvas: Canvas, speed: Int, gpsEnable: Boolean) {
        val xSpeedPos = canvas.width / 2.0f
        val ySpeedPos = canvas.height / 3.0f
        var speedText = "-"
        if (gpsEnable)
            speedText = speed.toString()
        canvas.drawText(speedText, xSpeedPos, ySpeedPos, speedPaint)
    }

    private fun drawText(canvas: Canvas, speedUnit: SpeedUnit) {
        val text = speedUnit.getString(context)
        val bounds = Rect()

        textPaint.getTextBounds(text, 0, text.length, bounds)

        val height: Int = bounds.height()

        val xTextPos = canvas.width / 2.0f
        val yTextPos = canvas.height / 3.0f + height + topTextPadding

        canvas.drawText(speedUnit.getString(context), xTextPos, yTextPos, textPaint)
    }

    override fun speedLimitExceeded() {
        speedPaint.color = context.getColor(R.color.alert_color)
    }

    override fun speedLimitReturned() {
        speedPaint.color = context.getColor(R.color.text_color)
    }
}