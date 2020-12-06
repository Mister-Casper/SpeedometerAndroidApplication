package com.sgc.speedometer.data.prefs

import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

interface PreferencesHelper {

    fun getMaxSpeed(defaultMaxSpeed:Int): Int

    fun setMaxSpeed(maxSpeed: Int)

    fun getSpeedUnit(defaultSpeedUnit: SpeedUnit): SpeedUnit

    fun setSpeedUnit(speedUnit: SpeedUnit)

    fun getIsDarkTheme(): Boolean

    fun setIsDarkTheme(isDarkTheme:Boolean)

    fun getIsVibration(): Boolean

    fun setIsVibration(isVibration:Boolean)
}