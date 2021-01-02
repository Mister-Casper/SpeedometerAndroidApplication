package com.sgc.speedometer

import android.app.Activity
import android.app.Application
import com.kobakei.ratethisapp.RateThisApp
import com.sgc.speedometer.di.component.*
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector {

    lateinit var appComponent: AppComponent
    lateinit var settingsComponent: SettingsComponent

    @set:Inject
    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null

    override fun onCreate() {
        super.onCreate()

        appComponent = DaggerAppComponent.builder()
            .application(this)
            .build()
        settingsComponent = DaggerSettingsComponent.builder()
            .appComponent(appComponent)
            .build()

        val config = RateThisApp.Config(3, 5)
        RateThisApp.init(config)
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

}