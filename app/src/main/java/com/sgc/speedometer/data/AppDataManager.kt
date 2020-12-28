package com.sgc.speedometer.data

import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataManager @Inject constructor(private val preferencesHelper: PreferencesHelper) : DataManager {

    override fun getMaxSpeed(defaultMaxSpeed: Int): Int {
        return preferencesHelper.getMaxSpeed(defaultMaxSpeed)
    }

    override fun setMaxSpeed(maxSpeed: Int) {
        preferencesHelper.setMaxSpeed(maxSpeed)
    }

    override fun getSpeedUnit(defaultSpeedUnit: SpeedUnit): SpeedUnit {
        return preferencesHelper.getSpeedUnit(defaultSpeedUnit)
    }

    override fun setSpeedUnit(speedUnit: SpeedUnit) {
        preferencesHelper.setSpeedUnit(speedUnit)
    }

    override fun getDistanceUnit(defaultDistanceUnit: DistanceUnit): DistanceUnit {
        return preferencesHelper.getDistanceUnit(defaultDistanceUnit)
    }

    override fun setDistanceUnit(distanceUnit: DistanceUnit) {
        preferencesHelper.setDistanceUnit(distanceUnit)
    }

    override fun getIsDarkTheme(): Boolean {
        return preferencesHelper.getIsDarkTheme()
    }

    override fun setIsDarkTheme(isDarkTheme: Boolean) {
        preferencesHelper.setIsDarkTheme(isDarkTheme)
    }

    override fun getIsVibration(): Boolean {
        return preferencesHelper.getIsVibration()
    }

    override fun setIsVibration(isVibration: Boolean) {
        preferencesHelper.setIsVibration(isVibration)
    }
}