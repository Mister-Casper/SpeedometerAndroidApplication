package com.sgc.speedometer.data.prefs

import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

interface PreferencesHelper {

    fun getMaxSpeed(defaultMaxSpeed:Int): Int

    fun setMaxSpeed(maxSpeed: Int)

    fun getSpeedUnit(defaultSpeedUnit: SpeedUnit): SpeedUnit

    fun setSpeedUnit(speedUnit: SpeedUnit)

    fun getDistanceUnit(defaultDistanceUnit: DistanceUnit): DistanceUnit

    fun setDistanceUnit(distanceUnit: DistanceUnit)

    fun getIsDarkTheme(defaultIsDarkTheme:Boolean): Boolean

    fun setIsDarkTheme(isDarkTheme:Boolean)

    fun getIsVibration(defaultIsVibration:Boolean): Boolean

    fun setIsVibration(isVibration:Boolean)
}