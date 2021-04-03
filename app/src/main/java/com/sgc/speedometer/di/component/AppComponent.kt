package com.sgc.speedometer.di.component

import android.app.Application
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.service.SpeedometerService
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.di.module.AppModule
import com.sgc.speedometer.di.module.DefaultSettingsModule
import com.sgc.speedometer.di.module.SpeedometerModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class, SpeedometerModule::class,DefaultSettingsModule::class])
interface AppComponent {

    fun inject(app: Application)

    fun inject(service: SpeedometerService)

    fun getDataManager(): DataManager
    fun getSpeedUnitConverter(): SpeedUnitConverter
    fun getDistanceUnitConverter(): DistanceUnitConverter

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}