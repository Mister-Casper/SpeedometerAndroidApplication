package com.sgc.speedometer.ui.customView.speedometer

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import com.sgc.speedometer.ui.customView.speedometer.render.RoundSpeedometerRender
import com.sgc.speedometer.ui.customView.speedometer.render.SpeedometerRender

class SpeedometerView : View {

    var defaultSettings = DefaultSettings()
    var speedometerRender: SpeedometerRender = RoundSpeedometerRender(context)
        set(value) {
            field = value
            invalidate()
            showSpeedLimitExceeded()
        }
    var speedUnit: SpeedUnit = defaultSettings.speedUnit
        set(value) {
            field = value
            invalidate()
        }
    var speed: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var maxSpeed: Int = 0
        set(value) {
            field = value
            invalidate()
        }
    var gpsEnable: Boolean = true
        set(value) {
            field = value
            invalidate()
        }
    var isSpeedLimitExceeded: Boolean = false
        set(value) {
            field = value
            showSpeedLimitExceeded()
        }
    var speedometerResolution = speedometerRender.speedometerResolution
        set(value) {
            if (value != field) {
                field = value
                speedometerRender.speedometerResolution = value
                invalidate()
            }
        }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    override fun onDraw(canvas: Canvas) {
        speedometerRender.draw(canvas, speed, maxSpeed, speedUnit, gpsEnable)
        super.onDraw(canvas)
    }

    private fun showSpeedLimitExceeded() {
        if (isSpeedLimitExceeded)
            speedometerRender.speedLimitExceeded()
        else
            speedometerRender.speedLimitReturned()
    }

}