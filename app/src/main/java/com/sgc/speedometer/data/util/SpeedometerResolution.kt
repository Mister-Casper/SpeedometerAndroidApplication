package com.sgc.speedometer.data.util

import android.content.Context
import com.sgc.speedometer.R

enum class SpeedometerResolution(val id: Int, val speedResolution: Int, private val stringId: Int) {
    First(0, 40, R.string.first_speed_resolution),
    Second(1, 80, R.string.second_speed_resolution),
    Third(2, 120, R.string.third_speed_resolution),
    Fourth(3, 160, R.string.fourth_speed_resolution),
    Fifth(4, 240, R.string.fifth_speed_resolution),
    Sixth(5, 320, R.string.sixth_speed_resolution);

    fun getString(context: Context): String {
        return context.getString(stringId)
    }

    companion object {
        fun getById(id: Int): SpeedometerResolution {
            for (e in values()) {
                if (e.id == id) return e
            }
            return Fifth
        }
    }
}