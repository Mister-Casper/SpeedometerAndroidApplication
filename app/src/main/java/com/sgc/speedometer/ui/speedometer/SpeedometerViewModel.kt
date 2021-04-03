package com.sgc.speedometer.ui.speedometer

import android.content.Context
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.model.SpeedometerRecord
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.base.BaseViewModel
import com.sgc.speedometer.ui.speedometer.speedLimitControl.SpeedLimitControl
import com.sgc.speedometer.ui.speedometer.speedLimitControl.SpeedLimitControlObserver

class SpeedometerViewModel(dataManager: DataManager, val speedUnitConverter: SpeedUnitConverter) :
    BaseViewModel(dataManager) {

    var maxSpeed: MutableLiveData<Int> = MutableLiveData<Int>(dataManager.getMaxSpeed())
    var speedometerRecord: MutableLiveData<SpeedometerRecord> = MutableLiveData<SpeedometerRecord>(SpeedometerRecord())

    private var speedLimitControl: SpeedLimitControl? = null

    fun updateMaxSpeed(maxSpeedValue: Int) {
        maxSpeed.value = maxSpeedValue
        dataManager.setMaxSpeed(maxSpeedValue)
        speedLimitControl?.speedLimit = maxSpeedValue
        speedLimitControl?.checkSpeedLimit(speedometerRecord.value!!.currentSpeed)
    }

    fun updateSpeedometerRecord(speedometerRecord: SpeedometerRecord) {
        this.speedometerRecord.value = speedometerRecord
        speedLimitControl?.checkSpeedLimit(
            speedUnitConverter.convertToDefaultByMetersPerSec(speedometerRecord.currentSpeed).toInt()
        )
    }

    fun setSpeedLimitControlObserver(speedLimitControlObserver: SpeedLimitControlObserver) {
        speedLimitControl = SpeedLimitControl(speedLimitControlObserver, dataManager.getMaxSpeed())
    }

    fun getIsGPSEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(AppCompatActivity.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}