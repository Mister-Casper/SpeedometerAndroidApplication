package com.sgc.speedometer.di.component

import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.di.module.ActivityModule
import com.sgc.speedometer.di.scope.ActivityScope
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.ui.speedometer.SpeedometerViewModel
import dagger.Component

@ActivityScope
@Component(modules = [ActivityModule::class], dependencies = [AppComponent::class])
interface ActivityComponent {

    fun getSpeedometerViewModel(): SpeedometerViewModel

    fun getDataManager(): DataManager

    fun inject(activity: SpeedometerActivity)
    
}