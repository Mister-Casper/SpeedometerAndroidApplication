package com.sgc.speedometer.ui.speedometer

import androidx.lifecycle.MutableLiveData
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.ui.base.BaseViewModel

class SpeedometerViewModel (dataManager: DataManager) : BaseViewModel(dataManager) {

    var maxSpeed: MutableLiveData<Int> = MutableLiveData<Int>(dataManager.getMaxSpeed(0))
    var statusGPS: MutableLiveData<Int> = MutableLiveData<Int>(0)

    fun updateMaxSpeed(maxSpeedValue : Int) {
        maxSpeed.value = maxSpeedValue
        dataManager.setMaxSpeed(maxSpeedValue)
    }

}