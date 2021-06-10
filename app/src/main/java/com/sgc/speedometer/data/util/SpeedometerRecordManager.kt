package com.sgc.speedometer.data.util

import android.location.Location
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.data.model.SpeedometerRecord
import mad.location.manager.lib.Loggers.GeohashRTFilter

class SpeedometerRecordManager(val speedometerRecord: SpeedometerRecord) {
    var lastLocation: Location? = null

    fun update(location: Location) {
        val currentSpeed: Double = location.speed.toDouble()

        if(lastLocation != null){
            val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
            speedometerRecord.distance += elapsedTimeInSeconds * currentSpeed
        }
        calcAverageSpeed()
        calcMaxSpeed(currentSpeed)
        speedometerRecord.currentSpeed = currentSpeed
        lastLocation = location
    }

    private fun calcMaxSpeed(currentSpeed: Double) {
        if (currentSpeed > speedometerRecord.maxSpeed)
            speedometerRecord.maxSpeed = currentSpeed
    }

    private fun calcAverageSpeed() {
        if (speedometerRecord.duration.getSeconds() != 0)
            speedometerRecord.averageSpeed = speedometerRecord.distance / speedometerRecord.duration.getSeconds()
    }

    fun reset() {
        speedometerRecord.duration = Date(0)
        speedometerRecord.currentSpeed = 0.0
        speedometerRecord.maxSpeed = 0.0
        speedometerRecord.distance = 0.0
        speedometerRecord.averageSpeed = 0.0
    }
}