package com.sgc.speedometer.di.module

import androidx.lifecycle.ViewModelProvider
import com.sgc.speedometer.ViewModelProviderFactory
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.ui.base.BaseActivity
import com.sgc.speedometer.ui.speedometer.SpeedometerViewModel
import dagger.Module
import dagger.Provides
import java.util.function.Supplier

@Module
class ActivityModule(private val activity: BaseActivity<*, *>) {

    @Provides
    fun provideSpeedometerViewModel(dataManager: DataManager): SpeedometerViewModel {
        val supplier: Supplier<SpeedometerViewModel> =
            Supplier<SpeedometerViewModel> { SpeedometerViewModel(dataManager) }
        val factory: ViewModelProviderFactory<SpeedometerViewModel> =
            ViewModelProviderFactory(SpeedometerViewModel::class.java, supplier)
        return ViewModelProvider(activity, factory).get(SpeedometerViewModel::class.java)
    }

}