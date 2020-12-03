package com.sgc.speedometer.di.module

import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import dagger.Module
import dagger.Provides

@Module
class SpeedometerModule {

    @Provides
    fun provideSpeedometerViewModel(dataManager: DataManager): SpeedUnitConverter {
        return SpeedUnitConverter(dataManager)
    }

}