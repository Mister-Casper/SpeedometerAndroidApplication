package com.sgc.speedometer.data.util

import android.location.Location
import android.os.CountDownTimer
import android.os.Message
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.data.model.SpeedometerRecord
import java.util.*
import java.util.logging.Handler
import java.util.logging.LogRecord

class SpeedometerRecordManager(val speedometerRecord: SpeedometerRecord) {
    private var lastLocation: Location? = null

    var sumSpeed: Double = 0.0
    var countSpeed: Int = 0

    fun update(location: Location) {
        if (lastLocation != null) {
            val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
            val distanceInMeters = location.distanceTo(lastLocation)

            val currentSpeed = if (location.hasSpeed() && location.speed > 0) {
                location.speed
            } else {
                distanceInMeters / elapsedTimeInSeconds
            }.toInt()
            speedometerRecord.currentSpeed = currentSpeed

            speedometerRecord.distance += distanceInMeters

            sumSpeed += currentSpeed
            countSpeed++
            speedometerRecord.averageSpeed = sumSpeed.toInt() / countSpeed
        }
        lastLocation = location
    }

    fun reset(){
        speedometerRecord.duration = Date(0)
        speedometerRecord.currentSpeed = 0
        speedometerRecord.distance = 0.0
        speedometerRecord.averageSpeed = 0
    }
}