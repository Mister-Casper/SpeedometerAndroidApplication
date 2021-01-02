package com.sgc.speedometer.di.module

import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import dagger.Module
import dagger.Provides

@Module
class SpeedometerModule {

    @Provides
    fun provideSpeedUnitConverter(dataManager: DataManager,defaultSettings: DefaultSettings): SpeedUnitConverter {
        return SpeedUnitConverter(dataManager,defaultSettings)
    }

    @Provides
    fun provideDistanceUnitConverter(dataManager: DataManager,defaultSettings: DefaultSettings): DistanceUnitConverter {
        return DistanceUnitConverter(dataManager,defaultSettings)
    }

}