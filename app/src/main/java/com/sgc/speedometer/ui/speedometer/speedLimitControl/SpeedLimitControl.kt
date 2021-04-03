package com.sgc.speedometer.ui.speedometer.speedLimitControl

class SpeedLimitControl(var speedLimitControlObserver: SpeedLimitControlObserver, var speedLimit: Int) {

    private var isSpeedLimitExceeded = false

    fun checkSpeedLimit(currentSpeed: Double) {
        if (currentSpeed > speedLimit) {
            if (!isSpeedLimitExceeded) {
                speedLimitControlObserver.speedLimitExceeded()
                isSpeedLimitExceeded = true
            }
        } else {
            if (isSpeedLimitExceeded) {
                speedLimitControlObserver.speedLimitReturned()
                isSpeedLimitExceeded = false
            }
        }
    }

}