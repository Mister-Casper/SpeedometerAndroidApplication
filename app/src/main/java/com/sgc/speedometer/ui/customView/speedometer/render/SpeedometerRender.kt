package com.sgc.speedometer.ui.customView.speedometer.render

import android.graphics.Canvas
import com.sgc.speedometer.utils.SpeedUnit

abstract class SpeedometerRender {

    abstract fun draw(canvas: Canvas, speed: Int, maxSpeed: Int, speedUnit: SpeedUnit)
}