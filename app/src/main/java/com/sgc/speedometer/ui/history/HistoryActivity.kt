package com.sgc.speedometer.ui.history

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.sgc.speedometer.App
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.SpeedometerRecord
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import com.sgc.speedometer.ui.speedometer.SpeedometerActivity
import com.sgc.speedometer.utils.AppConstants
import io.reactivex.android.schedulers.AndroidSchedulers
import kotlinx.android.synthetic.main.activity_history.*
import javax.inject.Inject

class HistoryActivity : AppCompatActivity(), HistoryAdapter.RecordListener {

    @Inject
    lateinit var dataManager: DataManager

    @Inject
    lateinit var distanceUnitConverter: DistanceUnitConverter

    @Inject
    lateinit var speedUnitConverter: SpeedUnitConverter

    lateinit var historyAdapter: HistoryAdapter

    @SuppressLint("CheckResult")
    override fun onCreate(savedInstanceState: Bundle?) {
        (this.application as App).appComponent.inject(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setHomeButtonEnabled(true)

        history.layoutManager = LinearLayoutManager(this)
        historyAdapter = HistoryAdapter(this, speedUnitConverter, dataManager, distanceUnitConverter, this)
        history.adapter = historyAdapter

        dataManager.getSpeedometerRecords()
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { records ->
                historyAdapter.records = records
                historyAdapter.notifyDataSetChanged()
            }
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                onBackPressed()
                true
            }
            else -> super.onOptionsItemSelected(menuItem)
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.left_to_right, R.anim.right_to_left)
    }

    override fun onDelete(record: SpeedometerRecord) {
        dataManager.deleteSpeedometerRecord(record)
    }

    override fun onContinue(record: SpeedometerRecord) {
        val intent = Intent(this, SpeedometerActivity::class.java)
        intent.putExtra(AppConstants.CONTINUE_RECORD,record)
        startActivity(intent)
    }
}