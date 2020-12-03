package com.sgc.speedometer.di.component

import com.sgc.speedometer.di.scope.ActivityScope
import com.sgc.speedometer.ui.settings.SettingsFragment
import dagger.Component

@ActivityScope
@Component(dependencies = [AppComponent::class])
interface SettingsComponent {

    fun inject(settingsFragment: SettingsFragment)

}