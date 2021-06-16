package com.sgc.speedometer;

parcelable SpeedometerRecord;

interface ISpeedometerService {
    void reset();
    void stop();
    void start();
    void continueRecord(in SpeedometerRecord record);
    void setRecordId(in long id);
}