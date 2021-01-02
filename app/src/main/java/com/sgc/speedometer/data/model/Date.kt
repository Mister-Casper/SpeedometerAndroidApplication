package com.sgc.speedometer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.*
import java.time.format.DateTimeFormatter

@Parcelize
data class Date(val epochMillis: Long) : Parcelable {
    companion object {
        var formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss")
    }

    override fun toString(): String {
        return formatter.format(
            Instant.ofEpochMilli(epochMillis).atZone(ZoneId.of("UTC"))
        )
    }

    operator fun plus(timeMillis: Number): Date {
        return Date(epochMillis + timeMillis.toInt())
    }

}