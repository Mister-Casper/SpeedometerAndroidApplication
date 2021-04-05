package com.sgc.speedometer.data.prefs

import android.content.Context
import android.content.SharedPreferences
import com.frybits.harmony.getHarmonySharedPreferences
import com.sgc.speedometer.data.util.SpeedometerResolution
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.di.PreferenceInfo
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import javax.inject.Inject

class AppPreferencesHelper @Inject constructor(context: Context, @PreferenceInfo prefFileName: String) :
    PreferencesHelper {

    companion object {
        private const val PREF_KEY_MAX_SPEED = "PREF_KEY_MAX_SPEED"
        private const val PREF_KEY_SPEED_UNIT = "PREF_KEY_SPEED_UNIT"
        private const val PREF_KEY_DISTANCE_UNIT = "PREF_KEY_DISTANCE_UNIT"
        private const val PREF_KEY_IS_DARK_THEME = "PREF_KEY_IS_DARK_THEME"
        private const val PREF_KEY_IS_VIBRATION = "PREF_KEY_IS_VIBRATION"
        private const val PREF_KEY_SPEEDOMETER_RESOLUTION= "PREF_KEY_SPEEDOMETER_RESOLUTION"
    }

    private val prefs: SharedPreferences =
        context.getHarmonySharedPreferences(prefFileName)

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

    override fun getDistanceUnit(defaultDistanceUnit: DistanceUnit): DistanceUnit {
        return DistanceUnit.getById(prefs.getInt(PREF_KEY_DISTANCE_UNIT, defaultDistanceUnit.id))
    }

    override fun setDistanceUnit(distanceUnit: DistanceUnit) {
        prefs.edit().putInt(PREF_KEY_DISTANCE_UNIT, distanceUnit.id).apply()
    }

    override fun getIsDarkTheme(defaultIsDarkTheme:Boolean): Boolean {
        return prefs.getBoolean(PREF_KEY_IS_DARK_THEME,defaultIsDarkTheme)
    }

    override fun setIsDarkTheme(isDarkTheme: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_IS_DARK_THEME, isDarkTheme).apply()
    }

    override fun getIsVibration(defaultIsVibration:Boolean): Boolean {
        return prefs.getBoolean(PREF_KEY_IS_VIBRATION,defaultIsVibration)
    }

    override fun setIsVibration(isVibration: Boolean) {
        prefs.edit().putBoolean(PREF_KEY_IS_VIBRATION, isVibration).apply()
    }

    override fun getSpeedometerResolution(defaultSpeedometerResolution: SpeedometerResolution): SpeedometerResolution {
        return SpeedometerResolution.getById(prefs.getInt(PREF_KEY_SPEEDOMETER_RESOLUTION, defaultSpeedometerResolution.id))
    }

    override fun setSpeedometerResolution(speedometerResolution: SpeedometerResolution) {
        prefs.edit().putInt(PREF_KEY_SPEEDOMETER_RESOLUTION, speedometerResolution.id).apply()
    }

}