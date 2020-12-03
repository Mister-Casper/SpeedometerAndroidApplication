package com.sgc.speedometer.ui.speedometer

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.InputType
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.WhichButton
import com.afollestad.materialdialogs.actions.getActionButton
import com.afollestad.materialdialogs.input.input
import com.sgc.speedometer.BR
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.service.SpeedometerService
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import com.sgc.speedometer.databinding.ActivitySpeedometerBinding
import com.sgc.speedometer.di.component.ActivityComponent
import com.sgc.speedometer.ui.base.BaseActivity
import com.sgc.speedometer.ui.customView.speedometer.speedLimitControl.SpeedLimitControlObserver
import com.sgc.speedometer.ui.settings.SettingsActivity
import com.sgc.speedometer.utils.AppConstants.SPEED_INTENT_FILTER
import com.sgc.speedometer.utils.AppConstants.SPEED_KEY
import com.sgc.speedometer.utils.AppConstants.TAG_CODE_PERMISSION_LOCATION
import kotlinx.android.synthetic.main.activity_speedometer.*
import javax.inject.Inject


class SpeedometerActivity : BaseActivity<ActivitySpeedometerBinding, SpeedometerViewModel>(),
    SpeedLimitControlObserver {

    override val bindingVariable: Int
        get() = BR.speedometerViewModel
    override val layoutId: Int
        get() = R.layout.activity_speedometer;

    private lateinit var receiver: SpeedReceiver
    private var vibrator: Vibrator? = null

    @Inject
    lateinit var dataManager: DataManager

    override fun performDependencyInjection(buildComponent: ActivityComponent) {
        buildComponent.inject(this);
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = ""
        selectTheme(dataManager.getIsDarkTheme())
        if (savedInstanceState == null) {
            requestPermissions()
        }
    }

    override fun onResume() {
        super.onResume()
        speedometer.speedUnit = dataManager.getSpeedUnit(SpeedUnit.KmPerHour)
    }

    private fun selectTheme(isDarkTheme: Boolean) {
        when (isDarkTheme) {
            true ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            false ->
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this, arrayOf(
                ACCESS_FINE_LOCATION,
                ACCESS_COARSE_LOCATION
            ),
            TAG_CODE_PERMISSION_LOCATION
        )
    }

    private fun registerReceiver() {
        receiver = SpeedReceiver()
        registerReceiver(receiver, IntentFilter(SPEED_INTENT_FILTER))
    }

    private fun initSpeedLimitClickListener() {
        speedLimit.setOnClickListener {
            MaterialDialog(this).show {
                title(R.string.set_speed_limit)
                negativeButton { }
                apply {
                    getActionButton(WhichButton.POSITIVE).updateTextColor(getColor(R.color.text_color))
                    getActionButton(WhichButton.NEGATIVE).updateTextColor(getColor(R.color.text_color))
                }
                input(
                    inputType = InputType.TYPE_CLASS_NUMBER,
                    prefill = viewModel.maxSpeed.value.toString()
                ) { _, text ->
                    viewModel.updateMaxSpeed(text.toString().toInt())
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            TAG_CODE_PERMISSION_LOCATION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initSpeedLimitClickListener()
                    registerReceiver()
                    viewModel.setSpeedLimitControlObserver(this)
                    startService()
                }
            }
        }
    }

    override fun speedLimitExceeded() {
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator!!.vibrate(4000)
        }
        speedometer.speedLimitExceeded()
    }

    override fun speedLimitReturned() {
        speedometer.speedLimitReturned()
        vibrator?.cancel()
    }

    override fun onDestroy() {
        stopService()
        vibrator?.cancel()
        super.onDestroy()
    }

    private fun startService() {
        val serviceIntent = Intent(this, SpeedometerService::class.java)
        ContextCompat.startForegroundService(this, serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, SpeedometerService::class.java)
        stopService(serviceIntent)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.speedometer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.getItemId()) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    inner class SpeedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(SPEED_INTENT_FILTER))
                viewModel.updateCurrentSpeed(intent.getIntExtra(SPEED_KEY, 0))
        }
    }
}