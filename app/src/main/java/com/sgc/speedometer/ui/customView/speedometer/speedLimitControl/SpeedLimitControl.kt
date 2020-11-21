package com.sgc.speedometer.ui.customView.speedometer.speedLimitControl

import android.util.Log
import com.sgc.speedometer.utils.TAG

class SpeedLimitControl(var speedLimitControlObserver: SpeedLimitControlObserver?, var speedLimit: Int) {

    fun checkSpeedLimit(currentSpeed: Int) {
        if (currentSpeed > speedLimit) {
            speedLimitControlObserver?.limitExceeded() ?: run {
                Log.e(TAG, "Cannot inform the observer about exceeding the speed limit because speedLimitControlObserver is null")
            }
        }
    }

}