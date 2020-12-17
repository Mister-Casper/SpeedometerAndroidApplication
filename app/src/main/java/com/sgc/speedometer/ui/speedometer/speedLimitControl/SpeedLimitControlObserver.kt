package com.sgc.speedometer.ui.speedometer.speedLimitControl

interface SpeedLimitControlObserver {

    fun speedLimitExceeded()
    fun speedLimitReturned()

}