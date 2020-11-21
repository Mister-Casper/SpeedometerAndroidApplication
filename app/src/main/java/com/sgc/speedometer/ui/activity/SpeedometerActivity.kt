package com.sgc.speedometer.ui.activity

import android.os.Bundle
import android.text.InputType
import androidx.activity.viewModels
import androidx.databinding.DataBindingUtil
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.sgc.speedometer.BR
import com.sgc.speedometer.R
import com.sgc.speedometer.databinding.ActivitySpeedometerBinding
import com.sgc.speedometer.di.component.ActivityComponent
import com.sgc.speedometer.ui.viewModel.SpeedometerViewModel
import kotlinx.android.synthetic.main.activity_speedometer.*

class SpeedometerActivity : BaseActivity<ActivitySpeedometerBinding, SpeedometerViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speedometer.speed = 120

        speedLimit.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.set_speed_limit)
                negativeButton {  }
                input(inputType = InputType.TYPE_CLASS_NUMBER,maxLength = 3) { _, text ->
                    viewModel.updateMaxSpeed(text.toString().toInt())
                }
            }
        }
    }

    override val bindingVariable: Int
        get() = BR.speedometerViewModel
    override val layoutId: Int
        get() =  R.layout.activity_speedometer;

    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this);
    }
}