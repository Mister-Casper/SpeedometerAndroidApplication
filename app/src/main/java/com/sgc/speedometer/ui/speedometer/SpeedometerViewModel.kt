package com.sgc.speedometer.ui.speedometer

import androidx.lifecycle.MutableLiveData
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.ui.base.BaseViewModel
import com.sgc.speedometer.ui.customView.speedometer.speedLimitControl.SpeedLimitControl
import com.sgc.speedometer.ui.customView.speedometer.speedLimitControl.SpeedLimitControlObserver

class SpeedometerViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {

    var maxSpeed: MutableLiveData<Int> = MutableLiveData<Int>(dataManager.getMaxSpeed())
    var currentSpeed: MutableLiveData<Int> = MutableLiveData<Int>(0)

    private var speedLimitControl: SpeedLimitControl? = null

    fun updateMaxSpeed(maxSpeedValue: Int) {
        maxSpeed.value = maxSpeedValue
        dataManager.setMaxSpeed(maxSpeedValue)
        speedLimitControl?.speedLimit = maxSpeedValue
    }

    fun updateCurrentSpeed(currentSpeedValue: Int) {
        currentSpeed.value = currentSpeedValue
        speedLimitControl?.checkSpeedLimit(currentSpeedValue)
    }

    fun setSpeedLimitControlObserver(speedLimitControlObserver: SpeedLimitControlObserver){
        speedLimitControl = SpeedLimitControl(speedLimitControlObserver,dataManager.getMaxSpeed())
    }
}