package com.sgc.speedometer.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.sgc.speedometer.App
import com.sgc.speedometer.di.component.ActivityComponent
import com.sgc.speedometer.di.component.DaggerActivityComponent
import com.sgc.speedometer.di.module.ActivityModule
import javax.inject.Inject

abstract class BaseActivity<T : ViewDataBinding, V : BaseViewModel> : AppCompatActivity() {

    var viewDataBinding: T? = null

    @Inject
    lateinit var viewModel: V

    abstract val layoutId: Int

    override fun onCreate(savedInstanceState: Bundle?) {
        performDependencyInjection(buildComponent)
        super.onCreate(savedInstanceState)
        performDataBinding()
    }

    abstract fun performDependencyInjection(buildComponent: ActivityComponent)

    private val buildComponent: ActivityComponent
        get() = DaggerActivityComponent.builder()
            .appComponent((application as App).appComponent)
            .activityModule(ActivityModule(this))
            .build()

    abstract fun performDataBinding()
}

