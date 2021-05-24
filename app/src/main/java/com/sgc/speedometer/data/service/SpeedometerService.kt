package com.sgc.speedometer.data.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import com.google.android.gms.common.ConnectionResult
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
import com.sgc.speedometer.utils.KalmanLatLong
import meetmehdi.interfaces.LocationManagerInterface
import meetmehdi.location.SmartLocationManager
import javax.inject.Inject

class SpeedometerService : Service(), LocationManagerInterface {

    private lateinit var smartLocationManager: SmartLocationManager

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    private var kalmanFilter = KalmanLatLong(1f)

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    @Inject
    lateinit var distanceUnitConverter: DistanceUnitConverter

    @Inject
    lateinit var dataManager: DataManager

    private var speedometerRecordManager: SpeedometerRecordManager =
        SpeedometerRecordManager(SpeedometerRecord())

    private var timer: CountDownTimer? = null

    private val binder = object : ISpeedometerService.Stub() {
        override fun reset() {
            this@SpeedometerService.reset()
        }

        override fun stop() {
            this@SpeedometerService.stop()
        }

        override fun start() {
            this@SpeedometerService.start()
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (application as App).appComponent.inject(this)
        manager = getSystemService(NotificationManager::class.java)
        initLocationFetching(2000,3000,0.1f, LocationRequest.PRIORITY_HIGH_ACCURACY)
        createNotificationChannel()
        createNotification()
        if (timer == null) {
            startTimer()
        }
        startForeground(1, createNotification())
        return START_STICKY
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
            .setSmallIcon(R.mipmap.icon_r)
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

    fun stop() {
        timer!!.cancel()
        smartLocationManager.abortLocationFetching()
    }

    fun start() {
        timer!!.start()
        initLocationFetching(2000,3000,0.1f, LocationRequest.PRIORITY_HIGH_ACCURACY)
    }

    private val runStartTimeInMillis = (SystemClock.elapsedRealtimeNanos() / 1000000)

    private fun filterLocation(location: Location): Location {
        val Qvalue: Float
        val locationTimeInMillis = (location.elapsedRealtimeNanos / 1000000)
        val elapsedTimeInMillis: Long = locationTimeInMillis - runStartTimeInMillis
        val currentSpeed = speedometerRecordManager.speedometerRecord.currentSpeed.toFloat()
        if (currentSpeed == 0.0f) {
            Qvalue = 2f
        } else {
            Qvalue = currentSpeed
        }
        kalmanFilter.Process(location.latitude, location.longitude, location.accuracy, elapsedTimeInMillis, Qvalue)
        val predictedLat: Double = kalmanFilter._lat
        val predictedLng: Double = kalmanFilter._lng
        val predictedLocation = Location("")

        predictedLocation.latitude = predictedLat
        predictedLocation.longitude = predictedLng
        predictedLocation.time = location.time

        val predictedDeltaInMeters: Float = predictedLocation.distanceTo(location)
        if (predictedDeltaInMeters > 60) {
            kalmanFilter.consecutiveRejectCount += 1
            if (kalmanFilter.consecutiveRejectCount > 3) {
                kalmanFilter = KalmanLatLong(1f)
            }
            return predictedLocation
        } else {
            kalmanFilter.consecutiveRejectCount = 0
        }
        return predictedLocation
    }

    companion object {
        const val CHANNEL_ID = "SPEEDOMETER_CHANNEL_ID_5"
    }

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                initLocationFetching(3500,7000,10f, LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
            } else if (intent.action == Intent.ACTION_SCREEN_ON) {
                initLocationFetching(2000,3000,0.1f, LocationRequest.PRIORITY_HIGH_ACCURACY)
            }
        }
    }

    private fun initLocationFetching(fastInterval:Long,interval:Long,distance:Float,priority:Int) {
        smartLocationManager = SmartLocationManager(
            this,
            SmartLocationManager.FETCH_LOCATION_BETWEEN_INTERVAL,
            this,
            SmartLocationManager.ALL_PROVIDERS,
            fastInterval,
            interval,
            SmartLocationManager.LOCATION_PROVIDER_ALL_RESTRICTION,
            SmartLocationManager.ANY_API,
            distance,priority
        )
    }

    override fun locationFetched(
        mLocation: Location?,
        oldLocation: Location?,
        time: String?,
        locationProvider: String?
    ) {
        if (mLocation != null) {
            speedometerRecordManager.update(mLocation)
            updateInfo(speedometerRecordManager.speedometerRecord)
        }
    }

    override fun onLocationFetchingFailed(failureType: Int, connectionResult: ConnectionResult?) {

    }

    override fun onLocationNotEnabled(message: String?) {

    }

    override fun onPermissionDenied(message: String?) {

    }


}