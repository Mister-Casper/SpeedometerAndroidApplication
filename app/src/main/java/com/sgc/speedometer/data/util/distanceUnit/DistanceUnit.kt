package com.sgc.speedometer.data.util.distanceUnit

import android.content.Context
import com.sgc.speedometer.R

enum class DistanceUnit(val id: Int, val valueFactor: Double, private val stringId: Int) {
    Meters(0, 1.0, R.string.m),
    Kms(1, 0.001, R.string.km),
    Miles(2, 0.000621371, R.string.mi);

    fun getString(context: Context): String {
        return context.getString(stringId)
    }

    companion object {
        fun getById(id: Int): DistanceUnit {
            for (e in values()) {
                if (e.id == id) return e
            }
            return Meters
        }
    }
}