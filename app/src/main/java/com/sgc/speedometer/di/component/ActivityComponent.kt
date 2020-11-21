package com.sgc.speedometer.di.component

import com.sgc.speedometer.di.module.ActivityModule
import com.sgc.speedometer.di.scope.ActivityScope
import com.sgc.speedometer.ui.activity.SpeedometerActivity
import dagger.Component

@ActivityScope
@Component(modules = [ActivityModule::class], dependencies = [AppComponent::class])
interface ActivityComponent {

    fun inject(activity: SpeedometerActivity)

}