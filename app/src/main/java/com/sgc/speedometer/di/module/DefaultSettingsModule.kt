package com.sgc.speedometer.di.module

import com.sgc.speedometer.data.DefaultSettings
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DefaultSettingsModule {

    @Provides
    @Singleton
    fun provideDefaultSettings(): DefaultSettings {
        return DefaultSettings()
    }

}