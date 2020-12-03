package com.sgc.speedometer.di.component

import com.sgc.speedometer.data.service.SpeedometerService
import com.sgc.speedometer.di.module.SpeedometerModule
import com.sgc.speedometer.di.scope.ActivityScope
import dagger.Component

@ActivityScope
@Component(modules = [SpeedometerModule::class], dependencies = [AppComponent::class])
interface SpeedometerComponent {

    fun inject(speedometerService: SpeedometerService)

}