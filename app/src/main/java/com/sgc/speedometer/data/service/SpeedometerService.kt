package com.sgc.speedometer.data.service

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.sgc.speedometer.App
import com.sgc.speedometer.R
import com.sgc.speedometer.data.model.SpeedometerRecord
import com.sgc.speedometer.data.util.SpeedometerRecordManager
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants.SPEEDOMETER_RECORD_KEY
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import javax.inject.Inject

class SpeedometerService : Service(), LocationListener {

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var locationManager: LocationManager

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    @Inject
    lateinit var distanceUnitConverter: DistanceUnitConverter

    private val speedometerRecordManager: SpeedometerRecordManager = SpeedometerRecordManager(SpeedometerRecord())

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        speedometerRecordManager.update(location)
        updateInfo(speedometerRecordManager.speedometerRecord)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startTimer()
        (application as App).appComponent.inject(this)
        manager = getSystemService(NotificationManager::class.java)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
        createNotification()
        startForeground(1, createNotification())
        turnOnGps()
        return START_NOT_STICKY
    }

    private fun startTimer(){
        val timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                speedometerRecordManager.speedometerRecord.duration += 1000
                updateInfo(speedometerRecordManager.speedometerRecord)
            }

            override fun onFinish() {}
        }
        timer.start()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, SpeedometerActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.speedometer_service_status))
            .setContentText(getString(R.string.speed_value, 0, 0))
            .setContentIntent(pendingIntent)
            .setSound(null)
            .setSmallIcon(R.mipmap.speedometer_icon)
        return builder.build()
    }

    private fun turnOnGps() {
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 50, 0.2f, this)
                }
            }
        }
    }

    private fun turnOffGps() {
        locationManager.removeUpdates(this)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val serviceChannel = NotificationChannel(
                CHANNEL_ID,
                "Foreground Service Channel",
                NotificationManager.IMPORTANCE_LOW
            )
            manager.createNotificationChannel(serviceChannel)
        }
    }

    override fun onDestroy() {
        turnOffGps()
        super.onDestroy()
    }

    private fun updateInfo(speedometerRecord: SpeedometerRecord) {
        updateNotification(speedometerRecord)
        sendDataToActivity(speedometerRecord)
    }

    private fun sendDataToActivity(speedometerRecord: SpeedometerRecord) {
        val sendIntent = Intent()
        sendIntent.action = SPEED_INTENT_FILTER
        sendIntent.putExtra(SPEEDOMETER_RECORD_KEY, speedometerRecord)
        sendBroadcast(sendIntent)
    }

    private fun updateNotification(speedometerRecord: SpeedometerRecord){
        val speed = speedUnitConverter.convertToDefaultByMetersPerSec(speedometerRecord.currentSpeed.toDouble()).toInt()
        val distance = distanceUnitConverter.convertToDefaultByMeters(speedometerRecord.distance).toInt()
        builder.setContentText(getString(R.string.speed_value, speed, distance))
        manager.notify(1, builder.build())
    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) {

    }

    companion object {
        const val CHANNEL_ID = "SPEEDOMETER_CHANNEL_ID_5"
    }
}