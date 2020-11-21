package com.sgc.speedometer.di.component

import android.app.Application
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import javax.inject.Singleton


@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    fun inject(app: Application)

    fun getDataManager(): DataManager

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder
        fun build(): AppComponent
    }
}