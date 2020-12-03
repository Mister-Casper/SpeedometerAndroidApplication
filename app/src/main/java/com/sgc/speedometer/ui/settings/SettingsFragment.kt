package com.sgc.speedometer.ui.settings

import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.ListPreference
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.sgc.speedometer.App
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import javax.inject.Inject

class SettingsFragment : PreferenceFragmentCompat() {

    @Inject
        lateinit var dataManager: DataManager

    override fun onCreate(savedInstanceState: Bundle?) {
        (requireActivity().application as App).settingsComponent.inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val themePreference: Preference? = findPreference("theme")

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
    }
}