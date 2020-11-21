package com.sgc.speedometer.ui.speedometer

import android.os.Bundle
import android.text.InputType
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.input.input
import com.sgc.speedometer.BR
import com.sgc.speedometer.R
import com.sgc.speedometer.databinding.ActivitySpeedometerBinding
import com.sgc.speedometer.di.component.ActivityComponent
import com.sgc.speedometer.ui.base.BaseActivity
import kotlinx.android.synthetic.main.activity_speedometer.*

class SpeedometerActivity : BaseActivity<ActivitySpeedometerBinding, SpeedometerViewModel>() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        speedometer.speed = 120

        speedLimit.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.set_speed_limit)
                negativeButton { }
                input(inputType = InputType.TYPE_CLASS_NUMBER, prefill = viewModel.maxSpeed.value.toString()) { _, text ->
                    viewModel.updateMaxSpeed(text.toString().toInt())
                }
            }
        }
    }

    override val bindingVariable: Int
        get() = BR.speedometerViewModel
    override val layoutId: Int
        get() = R.layout.activity_speedometer;

    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this);
    }
}