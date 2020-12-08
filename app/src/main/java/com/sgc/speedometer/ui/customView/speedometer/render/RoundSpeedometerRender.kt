package com.sgc.speedometer.ui.customView.speedometer.render

import android.content.Context
import android.graphics.*
import com.sgc.speedometer.R
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import java.lang.Math.toRadians
import kotlin.math.cos
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

class RoundSpeedometerRender(private val context: Context) : SpeedometerRender() {

    private var mainPaint = Paint()
    private var arrowPaint = Paint()
    private var speedPaint = Paint()
    private var textPaint = Paint()

    private var topTextPadding = 75

    private val bigGraduations = 10
    private val smallGraduations = 5

    private val maxSpeed = 240
    private val maxAngle = 240.0

    private val lengthBigGraduations = 50
    private val widthBigGraduations = 8
    private val lengthSmallGraduations = 25
    private val widthSmallGraduations = 5

    private val startAngle: Float = 150f

    init {
        mainPaint.textSize = 50.0f
        mainPaint.textAlign = Paint.Align.CENTER
        mainPaint.color = context.getColor(R.color.text_color)
        arrowPaint.color = context.getColor(R.color.alert_color)
        arrowPaint.strokeWidth = 10f
        speedPaint.textSize = 250.0f
        speedPaint.textAlign = Paint.Align.CENTER
        speedPaint.color = context.getColor(R.color.text_color)
        textPaint.textSize = 80.0f
        textPaint.textAlign = Paint.Align.CENTER
        textPaint.color = context.getColor(R.color.text_color)
    }

    override fun draw(canvas: Canvas, speed: Int, maxSpeed: Int, speedUnit: SpeedUnit, gpsEnable: Boolean) {
        drawGraduations(canvas)
        drawSpeed(canvas, speed, gpsEnable)
        if (gpsEnable) {
            drawArrow(canvas, speed)
            drawText(canvas, speedUnit)
        }
    }

    private fun drawGraduations(canvas: Canvas) {
        val countGraduations = bigGraduations * smallGraduations
        val graduationAngle = maxAngle / countGraduations
        val speedGraduation = maxSpeed / bigGraduations
        for (i in 0..countGraduations) {
            drawGraduation(
                canvas,
                i % smallGraduations == 0,
                startAngle + graduationAngle * i,
                speedGraduation * (i / (smallGraduations))
            )
        }
    }

    private fun drawGraduation(canvas: Canvas, isBig: Boolean, angle: Double, speed: Int) {
        val x = (canvas.width / 2 + getRadius(canvas) * cos(toRadians(angle))).toFloat()
        val y = (canvas.height / 2 + getRadius(canvas) * sin(toRadians(angle))).toFloat()

        val length = getGraduationLength(isBig)
        val width = getGraduationWidth(isBig)

        val rect = RectF(x - length / 2, y - width / 2, x + length / 2, y + width / 2)

        canvas.save()
        canvas.rotate(angle.toFloat(), rect.centerX(), rect.centerY())
        canvas.drawRect(rect, mainPaint)
        canvas.restore()

        if (isBig) {
            drawBigGraduationSpeed(canvas,speed,length,angle)
        }
    }

    private fun getGraduationLength(isBig:Boolean) : Int{
        return if (isBig) {
            lengthBigGraduations
        }else{
            lengthSmallGraduations
        }
    }

    private fun getGraduationWidth(isBig:Boolean) : Int{
        return if (isBig) {
            widthBigGraduations
        }else{
            widthSmallGraduations
        }
    }

    private fun drawBigGraduationSpeed(canvas: Canvas,speed: Int,length:Int,angle:Double){
        val textBounds = Rect()
        mainPaint.getTextBounds(speed.toString(), 0, speed.toString().length, textBounds)
        val x = (canvas.width / 2 + (getRadius(canvas) - length * 1.5) * cos(toRadians(angle))).toFloat()
        val y =
            (canvas.height / 2 + (getRadius(canvas) - length * 1.5) * sin(toRadians(angle))).toFloat() - textBounds.centerY()
        canvas.drawText(speed.toString(), x, y, mainPaint)
    }

    private fun drawSpeed(canvas: Canvas, speed: Int, gpsEnable: Boolean) {
        val xSpeedPos = canvas.width / 2f
        val ySpeedPos = canvas.height / 1.75f
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

        val xTextPos = canvas.width / 2f
        val yTextPos = canvas.height / 1.75f + height + topTextPadding

        canvas.drawText(speedUnit.getString(context), xTextPos, yTextPos, textPaint)
    }

    private fun drawArrow(canvas: Canvas, speed: Int) {
        var angle = startAngle.toDouble()
        if (speed != 0)
            angle = startAngle + (speed.toDouble() / maxSpeed * maxAngle)

        val bounds = Rect()
        textPaint.getTextBounds(speed.toString(), 0, speed.toString().length, bounds)

        val arrowRadius = max(bounds.height(), bounds.width()) * 2

        val xEnd = (canvas.width / 2 + getRadius(canvas) * cos(toRadians(angle))).toFloat()
        val yEnd = (canvas.height / 2 + getRadius(canvas) * sin(toRadians(angle))).toFloat()
        val xStart = (canvas.width / 2 + arrowRadius * cos(toRadians(angle))).toFloat()
        val yStart = (canvas.height / 2 + arrowRadius * sin(toRadians(angle))).toFloat()

        canvas.drawLine(xStart, yStart, xEnd, yEnd, arrowPaint)
    }

    private fun getRadius(canvas: Canvas): Int {
        return min(canvas.height, canvas.width) / 2 - lengthBigGraduations
    }

    override fun speedLimitExceeded() {
        speedPaint.color = context.getColor(R.color.alert_color)
    }

    override fun speedLimitReturned() {
        speedPaint.color = context.getColor(R.color.text_color)
    }

}