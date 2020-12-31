package com.sgc.speedometer.data

import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.SpeedometerResolution
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppDataManager @Inject constructor(private val preferencesHelper: PreferencesHelper): DataManager {

    private val defaultSettings = DefaultSettings()

    override fun setMaxSpeed(maxSpeed: Int) {
        preferencesHelper.setMaxSpeed(maxSpeed)
    }

    override fun getMaxSpeed(defaultMaxSpeed: Int): Int {
        return preferencesHelper.getMaxSpeed(defaultMaxSpeed)
    }

    override fun getMaxSpeed(): Int {
        return preferencesHelper.getMaxSpeed(defaultSettings.maxSpeed)
    }

    override fun setSpeedUnit(speedUnit: SpeedUnit) {
        preferencesHelper.setSpeedUnit(speedUnit)
    }

    override fun getSpeedUnit(defaultSpeedUnit: SpeedUnit): SpeedUnit {
        return preferencesHelper.getSpeedUnit(defaultSpeedUnit)
    }

    override fun getSpeedUnit(): SpeedUnit {
        return preferencesHelper.getSpeedUnit(defaultSettings.speedUnit)
    }

    override fun setDistanceUnit(distanceUnit: DistanceUnit) {
        preferencesHelper.setDistanceUnit(distanceUnit)
    }

    override fun getDistanceUnit(defaultDistanceUnit: DistanceUnit): DistanceUnit {
        return preferencesHelper.getDistanceUnit(defaultDistanceUnit)
    }

    override fun getDistanceUnit(): DistanceUnit {
        return preferencesHelper.getDistanceUnit(defaultSettings.distanceUnit)
    }

    override fun setIsDarkTheme(isDarkTheme: Boolean) {
        preferencesHelper.setIsDarkTheme(isDarkTheme)
    }

    override fun getIsDarkTheme(defaultIsDarkTheme: Boolean): Boolean {
        return preferencesHelper.getIsDarkTheme(defaultIsDarkTheme)
    }

    override fun getIsDarkTheme(): Boolean {
        return getIsDarkTheme(defaultSettings.isDarkTheme)
    }

    override fun setIsVibration(isVibration: Boolean) {
        preferencesHelper.setIsVibration(isVibration)
    }

    override fun getIsVibration(defaultIsVibration:Boolean): Boolean {
        return preferencesHelper.getIsVibration(defaultIsVibration)
    }

    override fun getIsVibration(): Boolean {
        return getIsDarkTheme(defaultSettings.isVibration)
    }

    override fun setSpeedometerResolution(speedometerResolution: SpeedometerResolution) {
        preferencesHelper.setSpeedometerResolution(speedometerResolution)
    }

    override fun getSpeedometerResolution(defaultSpeedometerResolution: SpeedometerResolution): SpeedometerResolution {
        return preferencesHelper.getSpeedometerResolution(defaultSpeedometerResolution)
    }

    override fun getSpeedometerResolution(): SpeedometerResolution {
      return getSpeedometerResolution(defaultSettings.speedometerResolution)
    }

}