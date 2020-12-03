package com.sgc.speedometer

import android.app.Activity
import android.app.Application
import com.sgc.speedometer.di.component.*
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    lateinit var appComponent: AppComponent
    lateinit var speedometerComponent: SpeedometerComponent
    lateinit var settingsComponent: SettingsComponent

    @set:Inject
    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        speedometerComponent = DaggerSpeedometerComponent.builder()
            .appComponent(appComponent)
            .build()
        settingsComponent = DaggerSettingsComponent.builder()
            .appComponent(appComponent)
            .build()
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

}