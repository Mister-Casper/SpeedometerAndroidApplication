package com.sgc.speedometer.data.service

import android.app.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.os.*
import androidx.core.app.NotificationCompat
import com.sgc.speedometer.App
import com.sgc.speedometer.ISpeedometerService
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.model.Date
import com.sgc.speedometer.SpeedometerRecord
import com.sgc.speedometer.data.util.SpeedometerRecordManager
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants.SPEEDOMETER_RECORD_KEY
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import mad.location.manager.lib.Commons.Utils
import mad.location.manager.lib.Interfaces.LocationServiceInterface
import mad.location.manager.lib.Services.KalmanLocationService
import mad.location.manager.lib.Services.ServicesHelper
import javax.inject.Inject

class SpeedometerService : Service(), LocationServiceInterface {

    val settings1 = KalmanLocationService.Settings(
        Utils.ACCELEROMETER_DEFAULT_DEVIATION,
        5,
        3000,
        0,
        0,
        2,
        null, false,
        Utils.DEFAULT_VEL_FACTOR, Utils.DEFAULT_POS_FACTOR
    )

    val settings2 = KalmanLocationService.Settings(
        Utils.ACCELEROMETER_DEFAULT_DEVIATION,
        40,
        9000,
        0,
        0,
        1,
        null, false,
        Utils.DEFAULT_VEL_FACTOR, Utils.DEFAULT_POS_FACTOR
    )

    private lateinit var builder: NotificationCompat.Builder
    private lateinit var manager: NotificationManager
    private lateinit var pendingIntent: PendingIntent

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    @Inject
    lateinit var distanceUnitConverter: DistanceUnitConverter

    @Inject
    lateinit var dataManager: DataManager

    private var speedometerRecordManager: SpeedometerRecordManager =
        SpeedometerRecordManager(SpeedometerRecord())

    private var timer: CountDownTimer? = null
    private var stopTime = 0L

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

        override fun continueRecord(record: SpeedometerRecord?) {
            speedometerRecordManager.speedometerRecord = record!!
        }

        override fun setRecordId(id: Long) {
            speedometerRecordManager.speedometerRecord.id = id
        }
    }

    override fun onBind(intent: Intent): IBinder {
        return binder
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        (application as App).appComponent.inject(this)
        manager = getSystemService(NotificationManager::class.java)
        registerReceiver()
        ServicesHelper.addLocationServiceInterface(this)
        reset(settings1)
        createNotificationChannel()
        createNotification()
        if (timer == null) {
            startTimer()
        }
        startForeground(1, createNotification())
        return START_STICKY
    }

    private fun reset(settings: KalmanLocationService.Settings) {
        ServicesHelper.getLocationService(this) { value: KalmanLocationService ->
            if (!value.IsRunning()) {
                value.stop()
                value.reset(settings)
                value.start()
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
        ServicesHelper.getLocationService(this) { value: KalmanLocationService ->
            value.stop()
        }
    }

    fun start() {
        timer!!.start()
        Handler(Looper.getMainLooper()).post {
            reset(settings1)
        }
    }

    companion object {
        const val CHANNEL_ID = "SPEEDOMETER_CHANNEL_ID_5"
    }

    val mReceiver: BroadcastReceiver = ScreenReceiver()

    private fun registerReceiver() {
        val filter = IntentFilter(Intent.ACTION_SCREEN_ON)
        filter.addAction(Intent.ACTION_SCREEN_OFF)
        registerReceiver(mReceiver, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mReceiver)
    }

    inner class ScreenReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action == Intent.ACTION_SCREEN_OFF) {
                reset(settings2)
                timer!!.cancel()
                stopTime = System.currentTimeMillis()
            } else if (intent.action == Intent.ACTION_SCREEN_ON) {
                reset(settings1)
                if (stopTime != 0L) {
                    speedometerRecordManager.speedometerRecord.duration =
                        Date(speedometerRecordManager.speedometerRecord.duration.epochMillis + System.currentTimeMillis() - stopTime)
                    startTimer()
                    speedometerRecordManager.calcAverageSpeed()
                }
            }
        }
    }

    override fun locationChanged(location: Location?) {
        if (location != null) {
            speedometerRecordManager.update(location)
            updateInfo(speedometerRecordManager.speedometerRecord)
        }
    }
}