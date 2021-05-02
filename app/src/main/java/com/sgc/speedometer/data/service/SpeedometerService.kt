package com.sgc.speedometer.data.service

import android.Manifest
import android.annotation.SuppressLint
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Criteria
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import com.google.android.gms.location.*
import com.sgc.speedometer.App
import com.sgc.speedometer.ISpeedometerService
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.model.SpeedometerRecord
import com.sgc.speedometer.data.util.SpeedometerRecordManager
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants.SPEEDOMETER_RECORD_KEY
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import javax.inject.Inject


class SpeedometerService : Service() {

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    @Inject
    lateinit var distanceUnitConverter: DistanceUnitConverter

    @Inject
    lateinit var dataManager: DataManager

    private lateinit var speedometerRecordManager: SpeedometerRecordManager

    private var timer: CountDownTimer? = null

    private val binder = object : ISpeedometerService.Stub() {
        override fun reset() {
            this@SpeedometerService.reset()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (application as App).appComponent.inject(this)
        if (timer == null) {
            speedometerRecordManager = SpeedometerRecordManager(SpeedometerRecord())
            startTimer()
        }
        manager = getSystemService(NotificationManager::class.java)
        requestLocationUpdates()
        createNotificationChannel()
        createNotification()
        startForeground(1, createNotification())
        return START_NOT_STICKY
    }

    @SuppressLint("MissingPermission")
    private fun requestLocationUpdates() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val mLocationRequest = LocationRequest()
        mLocationRequest.interval = 300
        mLocationRequest.fastestInterval = 50
        mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        fusedLocationClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.myLooper())
    }

    private var mLocationCallback: LocationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val locationList = locationResult.locations
            if (locationList.isNotEmpty()) {
                speedometerRecordManager.update(locationList.last())
                updateInfo(speedometerRecordManager.speedometerRecord)
            }
        }
    }

    private fun startTimer() {
        timer = object : CountDownTimer(Long.MAX_VALUE, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                speedometerRecordManager.speedometerRecord.duration += 1000
                updateInfo(speedometerRecordManager.speedometerRecord)
            }

            override fun onFinish() {}
        }
        timer!!.start()
    }

    private fun createNotification(): Notification {
        val notificationIntent = Intent(this, SpeedometerActivity::class.java)
        pendingIntent = PendingIntent.getActivity(
            this,
            0, notificationIntent, 0
        )

        builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(getString(R.string.speedometer_service_status))
            .setAutoCancel(true)
            .setStyle(
                NotificationCompat.BigTextStyle().bigText(
                    getString(
                        R.string.speed_value, 0, 0,
                        dataManager.getSpeedUnit().getString(this),
                        dataManager.getDistanceUnit().getString(this),
                        0, 0
                    )
                )
            )
            .setContentIntent(pendingIntent)
            .setSound(null)
            .setSmallIcon(R.mipmap.speedometer_icon)
        return builder.build()
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

    private fun updateInfo(speedometerRecord: SpeedometerRecord) {
        val km = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (!km.isKeyguardLocked)
            updateNotification(speedometerRecord)
        if ((application as App).isAppForeground)
            sendDataToActivity(speedometerRecord)
    }

    private fun sendDataToActivity(speedometerRecord: SpeedometerRecord) {
        val sendIntent = Intent()
        sendIntent.action = SPEED_INTENT_FILTER
        sendIntent.putExtra(SPEEDOMETER_RECORD_KEY, speedometerRecord)
        sendBroadcast(sendIntent)
    }

    private fun updateNotification(speedometerRecord: SpeedometerRecord) {
        val speed = speedUnitConverter.convertToDefaultByMetersPerSec(speedometerRecord.currentSpeed).toInt()
        val distance = distanceUnitConverter.convertToDefaultByMeters(speedometerRecord.distance).toInt()
        val average = speedUnitConverter.convertToDefaultByMetersPerSec(speedometerRecord.averageSpeed).toInt()
        val max = speedUnitConverter.convertToDefaultByMetersPerSec(speedometerRecord.maxSpeed).toInt()

        builder.setStyle(
            NotificationCompat.BigTextStyle().bigText(
                getString(
                    R.string.speed_value, speed, distance,
                    dataManager.getSpeedUnit().getString(this),
                    dataManager.getDistanceUnit().getString(this),
                    average, max
                )
            )
        )
        manager.notify(1, builder.build())
    }

    fun reset() {
        speedometerRecordManager.reset()
        updateInfo(speedometerRecordManager.speedometerRecord)
    }

    companion object {
        const val CHANNEL_ID = "SPEEDOMETER_CHANNEL_ID_5"
    }


}