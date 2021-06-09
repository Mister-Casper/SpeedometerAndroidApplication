package com.sgc.speedometer.data.util

import android.location.Location
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.data.model.SpeedometerRecord
import mad.location.manager.lib.Loggers.GeohashRTFilter
import kotlin.math.abs

class SpeedometerRecordManager(val speedometerRecord: SpeedometerRecord) {
    var lastLocation: Location? = null

    fun update(location: Location,geohashRTFilter:GeohashRTFilter) {
        val currentSpeed: Double?

        if (lastLocation != null) {
            currentSpeed = getCurrentSpeed(location)
            speedometerRecord.distance = geohashRTFilter.distanceGeoFiltered
            calcAverageSpeed()
        } else {
            currentSpeed = location.speed.toDouble()
        }

        if (currentSpeed != null) {
            calcMaxSpeed(currentSpeed)
            speedometerRecord.currentSpeed = currentSpeed
        }
        lastLocation = location
    }

   /* fun update(locations: List<Location>) {
        var currentSpeed: Double? = null
        locations.forEach {
            if (lastLocation != null) {
                speedometerRecord.distance += it.distanceTo(lastLocation)
                currentSpeed = getCurrentSpeed(it)
                if (currentSpeed != null) {
                    calcMaxSpeed(currentSpeed!!)
                }
            }
            lastLocation = it
        }
        calcAverageSpeed()
        if (currentSpeed != null)
            speedometerRecord.currentSpeed = currentSpeed!!
    }*/

    private fun getCurrentSpeed(location: Location): Double? {
        val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
        if (elapsedTimeInSeconds <= 0.03)
            return null
        return if (location.hasSpeed())
            location.speed.toDouble()
        else {
            val distanceInMeters = location.distanceTo(lastLocation)
            abs(distanceInMeters / elapsedTimeInSeconds)
        }
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