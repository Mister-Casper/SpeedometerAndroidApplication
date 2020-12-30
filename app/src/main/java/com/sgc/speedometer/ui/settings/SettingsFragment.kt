package com.sgc.speedometer.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.*
import com.sgc.speedometer.App
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnit
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
    lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).settingsComponent.inject(this)
        setDefaultSettings()
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val themePreference: SwitchPreference? = findPreference("theme")
        themePreference!!.setOnPreferenceChangeListener { _, o ->
            when (o) {
                true -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                }
                false -> {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
            dataManager.setIsDarkTheme(o as Boolean)
            true
        }

        val speedUnitPreference: ListPreference? = findPreference("speed_unit")
        speedUnitPreference!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val index: Int = speedUnitPreference.findIndexOfValue(newValue.toString())
                dataManager.setSpeedUnit(SpeedUnit.getById(index))
                true
            }

        val distanceUnitPreference: ListPreference? = findPreference("distance_unit")
        distanceUnitPreference!!.onPreferenceChangeListener =
            Preference.OnPreferenceChangeListener { _, newValue ->
                val index: Int = distanceUnitPreference.findIndexOfValue(newValue.toString())
                dataManager.setDistanceUnit(DistanceUnit.getById(index))
                true
            }

        val vibrationPreference: SwitchPreference? = findPreference("vibration")
        vibrationPreference!!.setOnPreferenceChangeListener { _, o ->
            dataManager.setIsVibration(o as Boolean)
            true
        }
    }

    private fun setDefaultSettings(){
        val prefs = PreferenceManager.getDefaultSharedPreferences(context).edit()
        prefs.putBoolean("theme", dataManager.getIsDarkTheme())
        prefs.putString("speed_unit", dataManager.getSpeedUnit().getString(requireContext()))
        prefs.putString("distance_unit", dataManager.getDistanceUnit().getString(requireContext()))
        prefs.putBoolean("vibration", dataManager.getIsVibration())
        prefs.apply()
    }
}