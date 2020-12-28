package com.sgc.speedometer.data.util.distanceUnit

import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

class DistanceUnitConverter (val preferencesHelper: PreferencesHelper){

    fun convertToDefaultByMeters(value: Double): Double {
        return preferencesHelper.getDistanceUnit(DistanceUnit.Meters).valueFactor * DistanceUnit.Meters.valueFactor * value
    }

}