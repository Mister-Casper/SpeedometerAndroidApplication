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
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.sgc.speedometer.App
import com.sgc.speedometer.R
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants.DISTANCE_KEY
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import com.sgc.speedometer.utils.AppConstants.SPEED_KEY
import javax.inject.Inject

class SpeedometerService : Service(), LocationListener {

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var locationManager: LocationManager

    private var lastLocation: Location? = null
    private var distance = 0.0

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        if (lastLocation != null) {
            val elapsedTimeInSeconds = (location.time - lastLocation!!.time) / 1000.0
            val distanceInMeters = location.distanceTo(lastLocation)
            val speed = distanceInMeters / elapsedTimeInSeconds

            val newSpeed = if (location.hasSpeed() && location.speed > 0) {
                speedUnitConverter.convertToDefaultByMetersPerSec(location.speed.toDouble())
            } else {
                speedUnitConverter.convertToDefaultByMetersPerSec(speed)
            }.toInt()

            distance += distanceInMeters
            updateInfo(newSpeed, distance.toInt())
        }
        lastLocation = location
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (application as App).speedometerComponent.inject(this)
        manager = getSystemService(NotificationManager::class.java)
        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        createNotificationChannel()
        createNotification()
        startForeground(1, createNotification())
        turnOnGps()
        return START_NOT_STICKY
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

    private fun updateInfo(speed: Int, distance: Int) {
        sendDataToActivity(speed, distance)
        builder.setContentText(getString(R.string.speed_value, speed, distance))
        manager.notify(1, builder.build())
    }

    private fun sendDataToActivity(speed: Int, distance: Int) {
        val sendIntent = Intent()
        sendIntent.action = SPEED_INTENT_FILTER
        sendIntent.putExtra(SPEED_KEY, speed)
        sendIntent.putExtra(DISTANCE_KEY, distance)
        sendBroadcast(sendIntent)
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