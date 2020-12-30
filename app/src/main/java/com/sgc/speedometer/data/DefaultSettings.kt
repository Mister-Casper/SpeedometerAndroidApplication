package com.sgc.speedometer.data

import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import java.util.*

class DefaultSettings {
    var maxSpeed = 60
    var speedUnit = SpeedUnit.KmPerHour
    var distanceUnit = DistanceUnit.Kms
    val isDarkTheme = true
    val isVibration = true

    init {
        if (Locale.getDefault().country == "US") {
            speedUnit = SpeedUnit.MilesPerHour
            distanceUnit = DistanceUnit.Miles
        }
    }
}