package com.sgc.speedometer.ui.speedometer

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.ui.base.BaseViewModel
import com.sgc.speedometer.ui.speedometer.speedLimitControl.SpeedLimitControl
import com.sgc.speedometer.ui.speedometer.speedLimitControl.SpeedLimitControlObserver

class SpeedometerViewModel(dataManager: DataManager) : BaseViewModel(dataManager) {

    var maxSpeed: MutableLiveData<Int> = MutableLiveData<Int>(dataManager.getMaxSpeed(60))
    var currentSpeed: MutableLiveData<Int> = MutableLiveData<Int>(0)

    private var speedLimitControl: SpeedLimitControl? = null

    fun updateMaxSpeed(maxSpeedValue: Int) {
        maxSpeed.value = maxSpeedValue
        dataManager.setMaxSpeed(maxSpeedValue)
        speedLimitControl?.speedLimit = maxSpeedValue
        speedLimitControl?.checkSpeedLimit(currentSpeed.value!!)
    }

    fun updateCurrentSpeed(currentSpeedValue: Int) {
        currentSpeed.value = currentSpeedValue
        speedLimitControl?.checkSpeedLimit(currentSpeedValue)
    }

    fun setSpeedLimitControlObserver(speedLimitControlObserver: SpeedLimitControlObserver){
        speedLimitControl = SpeedLimitControl(speedLimitControlObserver,dataManager.getMaxSpeed(60))
    }

    fun getIsGPSEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        val isGpsEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        val isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)

        return isGpsEnabled && isNetworkEnabled
    }
}