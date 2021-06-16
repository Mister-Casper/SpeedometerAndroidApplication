package com.sgc.speedometer.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.time.*
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.*

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

    fun toLongString(): String {
        val f = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT).withLocale(Locale.getDefault())
        return f.format(Instant.ofEpochMilli(epochMillis).atZone(ZoneId.of("UTC")))
    }

    operator fun plus(timeMillis: Number): Date {
        return Date(epochMillis + timeMillis.toInt())
    }

    fun getSeconds(): Int {
        return (epochMillis / 1000).toInt()
    }

}