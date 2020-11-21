package com.sgc.speedometer.utils

import android.content.Context
import com.sgc.speedometer.R

enum class SpeedUnit(val id: Int, val valueFactor: Double, private val stringId:Int) {
    KilometerPerHour(0, 1.0, R.string.km_per_hour),
    MilesPerHour(1, 0.621371, R.string.miles_per_hour);

    fun getString(context: Context):String{
        return context.getString(stringId)
    }

    companion object {
        fun getById(id:Int) : SpeedUnit{
            for (e in values()) {
                if (e.id == id) return e
            }
            return KilometerPerHour
        }
    }
}