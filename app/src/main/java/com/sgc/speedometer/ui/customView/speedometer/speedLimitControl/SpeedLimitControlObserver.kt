package com.sgc.speedometer.ui.customView.speedometer.speedLimitControl

interface SpeedLimitControlObserver {

    fun speedLimitExceeded()
    fun speedLimitReturned()

}