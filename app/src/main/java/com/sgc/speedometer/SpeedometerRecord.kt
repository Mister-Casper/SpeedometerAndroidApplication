package com.sgc.speedometer

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.sgc.speedometer.data.bd.util.DateConverter
import com.sgc.speedometer.data.model.Date
import kotlinx.android.parcel.Parcelize

@Entity
@Parcelize
data class SpeedometerRecord(
    @PrimaryKey(autoGenerate = true)
    var id:Long = 0,
    var recordDate: Date = Date(0),
    @TypeConverters(DateConverter::class)
    var duration: Date = Date(0),
    var distance: Double = 0.0,
    var averageSpeed: Double = 0.0,
    var maxSpeed:Double = 0.0,
    @Ignore
    var currentSpeed: Double = 0.0
) : Parcelable