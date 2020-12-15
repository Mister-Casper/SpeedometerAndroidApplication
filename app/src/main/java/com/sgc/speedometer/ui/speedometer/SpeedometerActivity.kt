package com.sgc.speedometer.ui.speedometer

import android.Manifest.permission.ACCESS_COARSE_LOCATION
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager.PROVIDERS_CHANGED_ACTION
import android.os.*
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
import com.kobakei.ratethisapp.RateThisApp
import com.sgc.speedometer.BR
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.data.service.SpeedometerService
import com.sgc.speedometer.data.util.speedUnit.SpeedUnit
import com.sgc.speedometer.databinding.ActivitySpeedometerBinding
import com.sgc.speedometer.di.component.ActivityComponent
import com.sgc.speedometer.ui.base.BaseActivity
import com.sgc.speedometer.ui.customView.speedometer.render.RoundSpeedometerRender
import com.sgc.speedometer.ui.customView.speedometer.render.TextSpeedometerRender
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
        initSpeedLimitClickListener()
        checkGPSEnable()
        if (savedInstanceState == null) {
            requestPermissions()
        }
        restoreState(savedInstanceState)
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

    private fun registerReceivers() {
        val speedReceiver = SpeedReceiver()
        registerReceiver(speedReceiver, IntentFilter(SPEED_INTENT_FILTER))
        val gpsSwitchStateReceiver = GPSSwitchStateReceiver()
        registerReceiver(gpsSwitchStateReceiver, IntentFilter(PROVIDERS_CHANGED_ACTION))
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
                    registerReceivers()
                    viewModel.setSpeedLimitControlObserver(this)
                    startService()
                    RateThisApp.onCreate(this)
                    RateThisApp.showRateDialogIfNeeded(this)
                }
            }
        }
    }

    override fun speedLimitExceeded() {
        if (dataManager.getIsVibration())
            vibrate()
        speedometer.isSpeedLimitExceeded = true
    }

    private fun vibrate() {
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator!!.vibrate(VibrationEffect.createOneShot(4000, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator!!.vibrate(4000)
        }
    }

    override fun speedLimitReturned() {
        vibrator?.cancel()
        speedometer.isSpeedLimitExceeded = false
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
        if (speedometer.speedometerRender.getRenderId() == 0)
            inflater.inflate(R.menu.round_speedometer_menu, menu)
        else
            inflater.inflate(R.menu.text_speedometer_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.settings -> {
                val intent = Intent(this, SettingsActivity::class.java)
                startActivity(intent)
            }
            R.id.set_round_speedometer -> {
                speedometer.speedometerRender = RoundSpeedometerRender(this)
                invalidateOptionsMenu()
            }
            R.id.set_text_speedometer -> {
                speedometer.speedometerRender = TextSpeedometerRender(this)
                invalidateOptionsMenu()
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun checkGPSEnable() {
        val isGPSEnable = viewModel.getIsGPSEnable(this)
        showGPSEnableDialog(isGPSEnable)
        speedometer.gpsEnable = isGPSEnable
    }

    private fun showGPSEnableDialog(isGPSEnable: Boolean) {
        if (!isGPSEnable) {
            if (!isFinishing)
                MaterialDialog(this@SpeedometerActivity).show {
                    title(R.string.gps_disable)
                    message(R.string.turn_on_gps)
                    negativeButton { }
                    positiveButton { startActivity(Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS)); }
                    apply {
                        getActionButton(WhichButton.POSITIVE).updateTextColor(getColor(R.color.text_color))
                        getActionButton(WhichButton.NEGATIVE).updateTextColor(getColor(R.color.text_color))
                    }
                }
        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putInt(SPEEDOMETER_RENDER_ID,speedometer.speedometerRender.getRenderId())
        super.onSaveInstanceState(outState)
    }

    private fun restoreState(state:Bundle?){
        if(state != null){
            val speedometerRenderId = state.getInt(SPEEDOMETER_RENDER_ID)
            if(speedometerRenderId == 1)
                speedometer.speedometerRender = TextSpeedometerRender(this)
        }
    }

    private inner class SpeedReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (intent.action.equals(SPEED_INTENT_FILTER))
                viewModel.updateCurrentSpeed(intent.getIntExtra(SPEED_KEY, 0))
        }
    }

    private inner class GPSSwitchStateReceiver : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (PROVIDERS_CHANGED_ACTION == intent.action) {
                checkGPSEnable()
            }
        }
    }

    companion object{
        private const val SPEEDOMETER_RENDER_ID = "SPEEDOMETER_RENDER_ID"
    }
}