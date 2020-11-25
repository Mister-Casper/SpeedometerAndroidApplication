package com.sgc.speedometer.ui.customView.speedometer.speedLimitControl

class SpeedLimitControl(var speedLimitControlObserver: SpeedLimitControlObserver, var speedLimit: Int) {

    private var isSpeedLimitExceeded = false
    private var isNotified = false

    fun checkSpeedLimit(currentSpeed: Int) {
        if (currentSpeed > speedLimit) {
            if(!isNotified) {
                speedLimitControlObserver.speedLimitExceeded()
                isSpeedLimitExceeded = true
                isNotified = true
            }
        } else {
            if (isSpeedLimitExceeded) {
                speedLimitControlObserver.speedLimitReturned()
                isNotified = false
            }
        }
    }

}