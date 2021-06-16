package com.sgc.speedometer.di.module

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.sgc.speedometer.data.AppDataManager
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.DefaultSettings
import com.sgc.speedometer.data.bd.AppDatabase
import com.sgc.speedometer.data.prefs.AppPreferencesHelper
import com.sgc.speedometer.data.prefs.PreferencesHelper
import com.sgc.speedometer.di.PreferenceInfo
import com.sgc.speedometer.utils.AppConstants
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun providePreferencesHelper(appPreferencesHelper: AppPreferencesHelper): PreferencesHelper {
        return appPreferencesHelper
    }

    @Provides
    @Singleton
    fun provideAppDatabase(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context, AppDatabase::class.java,
            "HISTORY_DATABASE"
        ).fallbackToDestructiveMigration()
            .allowMainThreadQueries().build()
    }

    @Provides
    @Singleton
    fun provideDataManager(
        preferencesHelper: PreferencesHelper,
        defaultSettings: DefaultSettings,
        appDatabase: AppDatabase
    ): DataManager {
        return AppDataManager(preferencesHelper, defaultSettings, appDatabase)
    }

    @Provides
    @Singleton
    fun provideContext(application: Application): Context {
        return application
    }

    @Provides
    @PreferenceInfo
    fun providePreferenceName(): String {
        return AppConstants.PREF_NAME
    }

}