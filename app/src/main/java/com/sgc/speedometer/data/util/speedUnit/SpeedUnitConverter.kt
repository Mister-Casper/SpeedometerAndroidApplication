package com.sgc.speedometer.data.util.speedUnit

import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.prefs.PreferencesHelper

class SpeedUnitConverter(val preferencesHelper: PreferencesHelper,private val defaultSettings: DefaultSettings) {

    fun convertToDefaultByMetersPerSec(value: Number): Number {
        return preferencesHelper.getSpeedUnit(defaultSettings.speedUnit).valueFactor * SpeedUnit.MetersPerSec.valueFactor * value.toDouble()
    }

}