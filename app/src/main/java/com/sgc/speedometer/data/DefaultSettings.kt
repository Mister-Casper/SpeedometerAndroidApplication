package com.sgc.speedometer.data

import com.sgc.speedometer.data.util.SpeedometerResolution
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import java.util.*
import javax.inject.Singleton

@Singleton
class DefaultSettings {
    var maxSpeed = 80
    var speedUnit = SpeedUnit.KmPerHour
    val speedometerResolution: SpeedometerResolution = SpeedometerResolution.Fifth
    var distanceUnit = DistanceUnit.Kms
    val isDarkTheme = true
    val isVibration = false

    init {
        if (Locale.getDefault().country == "US") {
            speedUnit = SpeedUnit.MilesPerHour
            distanceUnit = DistanceUnit.Miles
        }
    }
}