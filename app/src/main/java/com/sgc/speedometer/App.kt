package com.sgc.speedometer

import android.app.Activity
import android.app.Application
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import com.kobakei.ratethisapp.RateThisApp
import com.sgc.speedometer.di.component.*
import dagger.android.DispatchingAndroidInjector
import dagger.android.HasActivityInjector
import javax.inject.Inject

class App : Application(), HasActivityInjector, LifecycleObserver {

    lateinit var appComponent: AppComponent
    lateinit var settingsComponent: SettingsComponent

    @set:Inject
    var activityDispatchingAndroidInjector: DispatchingAndroidInjector<Activity>? = null

    var isAppForeground = true

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
        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    fun onAppBackgrounded() {
        isAppForeground = false
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppForegrounded() {
        isAppForeground = true
    }

    override fun activityInjector(): DispatchingAndroidInjector<Activity>? {
        return activityDispatchingAndroidInjector
    }

}