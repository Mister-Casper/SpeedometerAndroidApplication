package com.sgc.speedometer.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.sgc.speedometer.di.PreferenceInfo
import com.sgc.speedometer.data.util.SpeedUnit.SpeedUnit
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(context: Context, @PreferenceInfo prefFileName: String) :
    PreferencesHelper {

    companion object {
        private const val PREF_KEY_MAX_SPEED = "PREF_KEY_MAX_SPEED"
        private const val PREF_KEY_SPEED_UNIT = "PREF_KEY_SPEED_UNIT"
    }

    private val prefs: SharedPreferences =
        context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE)

    override fun getMaxSpeed(defaultMaxSpeed: Int): Int {
        return prefs.getInt(PREF_KEY_MAX_SPEED, defaultMaxSpeed)
    }

    override fun setMaxSpeed(maxSpeed: Int) {
        prefs.edit().putInt(PREF_KEY_MAX_SPEED, maxSpeed).apply()
    }

    override fun getSpeedUnit(defaultSpeedUnit: SpeedUnit): SpeedUnit {
        return SpeedUnit.getById(prefs.getInt(PREF_KEY_SPEED_UNIT, defaultSpeedUnit.id))
    }

    override fun setSpeedUnit(speedUnit: SpeedUnit) {
        prefs.edit().putInt(PREF_KEY_SPEED_UNIT, speedUnit.id).apply()
    }
}