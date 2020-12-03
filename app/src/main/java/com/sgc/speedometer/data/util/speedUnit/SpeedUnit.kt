package com.sgc.speedometer.data.util.speedUnit

import android.content.Context
import com.sgc.speedometer.R

enum class SpeedUnit(val id: Int, val valueFactor: Double, private val stringId:Int) {
    MetersPerSec(0, 1.0, R.string.m_per_sec),
    KmPerHour(1, 3.6, R.string.km_per_hour),
    MilesPerHour(2, 2.23694, R.string.miles_per_hour);

    fun getString(context: Context):String{
        return context.getString(stringId)
    }

    companion object {
        fun getById(id:Int) : SpeedUnit {
            for (e in values()) {
                if (e.id == id) return e
            }
            return MetersPerSec
        }
    }
}