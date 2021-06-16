package com.sgc.speedometer.ui.history

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.sgc.speedometer.R
import com.sgc.speedometer.data.DataManager
import com.sgc.speedometer.SpeedometerRecord
import com.sgc.speedometer.data.util.distanceUnit.DistanceUnitConverter
import com.sgc.speedometer.data.util.speedUnit.SpeedUnitConverter
import kotlinx.android.synthetic.main.record_item.view.*
import java.util.*

class HistoryAdapter(
    private val context: Context,
    private val speedUnitConverter: SpeedUnitConverter, private val dataManager: DataManager,
    private val distanceUnitConverter: DistanceUnitConverter, private val recordListener: RecordListener
) :
    RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    var records: List<SpeedometerRecord> = Collections.emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.record_item, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(records[position])
    }

    override fun getItemCount(): Int {
        return records.size
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {

        @SuppressLint("SetTextI18n")
        fun bind(record: SpeedometerRecord) {
            itemView.record_id.text = record.id.toString() + "."
            itemView.date.text = record.recordDate.toLongString()
            itemView.duration.text =
                context.getString(R.string.speed_info_duration) + " : " + record.duration.toString()
            itemView.max.text =
                context.getString(R.string.speed_info_max) + " : " + speedUnitConverter.convertToDefaultByMetersPerSec(
                    record.maxSpeed
                ).toInt()
                    .toString() + " " + dataManager.getSpeedUnit().getString(context)
            itemView.av.text =
                context.getString(R.string.speed_info_average) + " : " + speedUnitConverter.convertToDefaultByMetersPerSec(
                    record.averageSpeed
                ).toInt()
                    .toString() + " " + dataManager.getSpeedUnit().getString(context)
            itemView.distance.text =
                context.getString(R.string.speed_info_distance) + " : " + distanceUnitConverter.convertToDefaultByMeters(
                    record.distance
                ).toInt()
                    .toString() + " " + dataManager.getDistanceUnit().getString(context)
            itemView.delete.setOnClickListener { recordListener.onDelete(record) }
            itemView.continue_record.setOnClickListener { recordListener.onContinue(record) }
        }
    }

    interface RecordListener {
        fun onDelete(record: SpeedometerRecord)
        fun onContinue(record: SpeedometerRecord)
    }
}