package com.sgc.speedometer.ui.customView.speedometer.render

import android.graphics.Canvas
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

abstract class SpeedometerRender {

    abstract fun draw(canvas: Canvas, speed: Int, maxSpeed: Int, speedUnit: SpeedUnit,gpsEnable:Boolean)
    abstract fun speedLimitExceeded()
    abstract fun speedLimitReturned()
}