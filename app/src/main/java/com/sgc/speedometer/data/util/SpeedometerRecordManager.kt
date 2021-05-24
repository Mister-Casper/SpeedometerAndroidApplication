package com.sgc.speedometer.data.util

import android.location.Location
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.data.model.SpeedometerRecord
import kotlin.math.abs

class SpeedometerRecordManager(val speedometerRecord: SpeedometerRecord) {
    private var lastLocation: Location? = null

    fun update(location: Location) {
        val currentSpeed: Double?

        if (lastLocation != null) {
            val distanceInMeters = location.distanceTo(lastLocation)
            currentSpeed = getCurrentSpeed(location)
            speedometerRecord.distance += distanceInMeters
        } else {
            currentSpeed = location.speed.toDouble()
        }

        if (speedometerRecord.duration.getSeconds() != 0)
            speedometerRecord.averageSpeed = speedometerRecord.distance / speedometerRecord.duration.getSeconds()

        if(currentSpeed != null) {
            calcMaxSpeed(currentSpeed)
            speedometerRecord.currentSpeed = currentSpeed
        }
        lastLocation = location
    }

    private fun getCurrentSpeed(location: Location): Double? {
        val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
        if(elapsedTimeInSeconds <= 0.02)
            return null
        val distanceInMeters = location.distanceTo(lastLocation)
        return abs(distanceInMeters / elapsedTimeInSeconds)
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
        speedometerRecord.averageSpeed = 0.0
    }
}