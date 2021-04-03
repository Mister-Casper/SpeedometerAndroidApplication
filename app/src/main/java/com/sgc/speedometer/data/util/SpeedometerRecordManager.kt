package com.sgc.speedometer.data.util

import android.location.Location
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.data.model.SpeedometerRecord

class SpeedometerRecordManager(val speedometerRecord: SpeedometerRecord) {
    private var lastLocation: Location? = null

    var sumSpeed: Double = 0.0
    var countSpeed: Int = 0

    fun update(location: Location) {
        if (lastLocation != null) {
            val distanceInMeters = location.distanceTo(lastLocation)
            val currentSpeed = getCurrentSpeed(location)
            speedometerRecord.currentSpeed = currentSpeed
            speedometerRecord.distance += distanceInMeters
            calcAverageSpeed(currentSpeed)
            calcMaxSpeed(currentSpeed)
        }
        lastLocation = location
    }

    private fun getCurrentSpeed(location: Location): Double {
        val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
        val distanceInMeters = location.distanceTo(lastLocation)

        return if (location.hasSpeed() && location.speed > 0) {
            location.speed
        } else {
            distanceInMeters / elapsedTimeInSeconds
        }.toDouble()
    }

    private fun calcAverageSpeed(currentSpeed: Double) {
        sumSpeed += currentSpeed
        countSpeed++
        speedometerRecord.averageSpeed = sumSpeed.toInt() / countSpeed
    }

    private fun calcMaxSpeed(currentSpeed: Double) {
        if (currentSpeed > speedometerRecord.maxSpeed)
            speedometerRecord.maxSpeed = currentSpeed
    }

    fun reset() {
        speedometerRecord.duration = Date(0)
        speedometerRecord.currentSpeed = 0.0
        speedometerRecord.maxSpeed = 0.0
        speedometerRecord.distance = 0.0
        speedometerRecord.averageSpeed = 0
    }
}