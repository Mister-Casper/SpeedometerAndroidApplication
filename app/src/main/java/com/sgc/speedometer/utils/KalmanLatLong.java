package com.sgc.speedometer.utils;

public class KalmanLatLong {
    private final float MinAccuracy = 1;

    private float Q_metres_per_second;
    private long TimeStamp_milliseconds;
    private double lat;
    private double lng;
    private float variance;
    public int consecutiveRejectCount;

    public KalmanLatLong(float Q_metres_per_second) {
        this.Q_metres_per_second = Q_metres_per_second;
        variance = -1;
        consecutiveRejectCount = 0;
    }

    public long get_TimeStamp() {
        return TimeStamp_milliseconds;
    }

    public double get_lat() {
        return lat;
    }

    public double get_lng() {
        return lng;
    }

    public float get_accuracy() {
        return (float) Math.sqrt(variance);
    }

    public void SetState(double lat, double lng, float accuracy,
                         long TimeStamp_milliseconds) {
        this.lat = lat;
        this.lng = lng;
        variance = accuracy * accuracy;
        this.TimeStamp_milliseconds = TimeStamp_milliseconds;
    }

    public void Process(double lat_measurement, double lng_measurement,
                        float accuracy, long TimeStamp_milliseconds, float Q_metres_per_second) {
        this.Q_metres_per_second = Q_metres_per_second;

        if (accuracy < MinAccuracy)
            accuracy = MinAccuracy;
        if (variance < 0) {
            this.TimeStamp_milliseconds = TimeStamp_milliseconds;
            lat = lat_measurement;
            lng = lng_measurement;
            variance = accuracy * accuracy;
        } else {
            long TimeInc_milliseconds = TimeStamp_milliseconds
                    - this.TimeStamp_milliseconds;
            if (TimeInc_milliseconds > 0) {
                variance += TimeInc_milliseconds * Q_metres_per_second
                        * Q_metres_per_second / 1000;
                this.TimeStamp_milliseconds = TimeStamp_milliseconds;
            }
            float K = variance / (variance + accuracy * accuracy);
            lat += K * (lat_measurement - lat);
            lng += K * (lng_measurement - lng);
            variance = (1 - K) * variance;
        }
    }

    public int getConsecutiveRejectCount() {
        return consecutiveRejectCount;
    }

    public void setConsecutiveRejectCount(int consecutiveRejectCount) {
        this.consecutiveRejectCount = consecutiveRejectCount;
    }
}
