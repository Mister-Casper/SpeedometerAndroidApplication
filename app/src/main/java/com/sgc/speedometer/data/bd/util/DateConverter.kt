package com.sgc.speedometer.data.bd.util

import androidx.room.TypeConverter
import com.sgc.speedometer.data.model.Date

class DateConverter {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.epochMillis
    }

    @TypeConverter
    fun toDate(time: Long): Date {
        return Date(time)
    }

}