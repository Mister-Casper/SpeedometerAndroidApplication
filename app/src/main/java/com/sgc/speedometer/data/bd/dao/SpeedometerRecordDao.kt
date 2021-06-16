package com.sgc.speedometer.data.bd.dao

import androidx.room.*
import com.sgc.speedometer.SpeedometerRecord
import io.reactivex.Flowable

@Dao
interface SpeedometerRecordDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertSpeedometerRecord(speedometerRecord: SpeedometerRecord):Long

    @Update
    fun updateSpeedometerRecord(speedometerRecord: SpeedometerRecord)

    @Delete
    fun deleteSpeedometerRecord(speedometerRecord: SpeedometerRecord)

    @Query("SELECT * FROM SpeedometerRecord")
    fun getSpeedometerRecords(): Flowable<List<SpeedometerRecord>>

}