package com.sgc.speedometer.data

import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

interface DataManager : PreferencesHelper {

    fun getMaxSpeed(): Int
    fun getSpeedUnit(): SpeedUnit
    fun getDistanceUnit(): DistanceUnit
    fun getIsDarkTheme(): Boolean
    fun getIsVibration(): Boolean

}