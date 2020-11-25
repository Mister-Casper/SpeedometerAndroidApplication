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
import android.os.IBinder
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.sgc.speedometer.R
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import com.sgc.speedometer.utils.AppConstants.SPEED_KEY

class SpeedometerService : Service(), LocationListener {

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var locationManager: LocationManager

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onLocationChanged(location: Location) {
        updateSpeed((location.speed * 3.6f).toInt())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
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
            .setContentTitle("Спидометр работает в фоновом режиме")
            .setContentText("Скорость - 0 км/ч")
            .setContentIntent(pendingIntent)
            .setSound(null)
            .setSmallIcon(R.mipmap.ic_launcher_round)
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
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 100, 1f, this)
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

    private fun updateSpeed(speed: Int) {
        sendDataToActivity(speed)
        builder.setContentText("Скорость - $speed км/ч")
        manager.notify(1, builder.build())
    }

    private fun sendDataToActivity(speed: Int) {
        val sendIntent = Intent()
        sendIntent.action = SPEED_INTENT_FILTER
        sendIntent.putExtra(SPEED_KEY, speed)
        sendBroadcast(sendIntent)
    }

    override fun onProviderEnabled(provider: String) {

    }

    override fun onProviderDisabled(provider: String) {

    }

    companion object {
        const val CHANNEL_ID = "SPEEDOMETER_CHANNEL_ID_5"
    }
}