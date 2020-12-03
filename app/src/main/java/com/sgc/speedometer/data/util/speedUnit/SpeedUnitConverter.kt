package com.sgc.speedometer.data.util.speedUnit

import com.sgc.speedometer.data.prefs.PreferencesHelper

class SpeedUnitConverter(val preferencesHelper: PreferencesHelper) {

    fun convertToDefaultByMetersPerSec(value: Double): Double {
        return preferencesHelper.getSpeedUnit(SpeedUnit.KmPerHour).valueFactor * SpeedUnit.MetersPerSec.valueFactor * value
    }

}