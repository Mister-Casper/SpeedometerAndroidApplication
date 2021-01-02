package com.sgc.speedometer.data.util.distanceUnit

import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

class DistanceUnitConverter (val preferencesHelper: PreferencesHelper,private val defaultSettings: DefaultSettings){

    fun convertToDefaultByMeters(value: Number): Number {
        return preferencesHelper.getDistanceUnit(defaultSettings.distanceUnit).valueFactor * DistanceUnit.Meters.valueFactor * value.toDouble()
    }

}