package com.sgc.speedometer.data.bd

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sgc.speedometer.data.bd.dao.SpeedometerRecordDao
import com.sgc.speedometer.data.bd.util.DateConverter
import com.sgc.speedometer.SpeedometerRecord
import javax.inject.Singleton

@Singleton
@Database(entities = [SpeedometerRecord::class], version = 3)
@TypeConverters(DateConverter::class)
    abstract class AppDatabase : RoomDatabase() {
    abstract fun speedometerRecordDao(): SpeedometerRecordDao
}