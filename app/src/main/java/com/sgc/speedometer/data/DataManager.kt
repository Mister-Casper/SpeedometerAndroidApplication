package com.sgc.speedometer.data

import com.sgc.speedometer.data.bd.dao.SpeedometerRecordDao
import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.data.util.SpeedometerResolution
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit

interface DataManager : PreferencesHelper, SpeedometerRecordDao {

    fun getMaxSpeed(): Int
    fun getSpeedUnit(): SpeedUnit
    fun getDistanceUnit(): DistanceUnit
    fun getIsDarkTheme(): Boolean
    fun getIsVibration(): Boolean
    fun getSpeedometerResolution(): SpeedometerResolution

}