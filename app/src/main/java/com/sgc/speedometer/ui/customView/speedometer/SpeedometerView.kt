package com.sgc.speedometer.ui.customView.speedometer

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.view.View
import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import com.sgc.speedometer.ui.customView.speedometer.render.RoundSpeedometerRender
import com.sgc.speedometer.ui.customView.speedometer.render.SpeedometerRender
import java.util.*


class SpeedometerView : View {

    private var isUpdated = false

    var defaultSettings = DefaultSettings()
    var speedometerRender: SpeedometerRender = RoundSpeedometerRender(context)
        set(value) {
            field = value
            isUpdated = true
            showSpeedLimitExceeded()
        }
    var speedUnit: SpeedUnit = defaultSettings.speedUnit
        set(value) {
            field = value
            isUpdated = true
        }
    var speed: Int = 0
        set(value) {
            field = value
            isUpdated = true
        }
    var maxSpeed: Int = 0
        set(value) {
            field = value
            isUpdated = true
        }
    var gpsEnable: Boolean = true
        set(value) {
            field = value
            isUpdated = true
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
                isUpdated = true
            }
        }

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : this(context, attrs, 0)

    init {
        Timer().scheduleAtFixedRate(object : TimerTask() {
            override fun run() {
                if (isUpdated) {
                    isUpdated = false
                    postInvalidate()
                }
            }
        }, 0, 500)
    }

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