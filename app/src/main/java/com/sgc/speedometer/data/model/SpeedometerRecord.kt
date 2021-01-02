package com.sgc.speedometer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SpeedometerRecord(
    var duration: Date = Date(0),
    var distance: Double = 0.0,
    var averageSpeed: Int = 0,
    var currentSpeed: Int = 0
) : Parcelable