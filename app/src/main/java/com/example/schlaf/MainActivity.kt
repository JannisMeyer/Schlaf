package com.example.schlaf

import android.content.ContentValues.TAG
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.schlaf.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // TODO: add comments
    // TODO: add persistence of data
    // TODO: add averages below graph and to graph
    // TODO: add modify functionality to bars
    // TODO: add todays bar functionality

    private lateinit var binding: ActivityMainBinding


    private var barChart: BarChart = binding.barChart

    companion object {

        private lateinit var sleepDataGlobal : MutableList<Sleep>
        private lateinit var startDate : LocalDate
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect this activity with corresponding display
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getSleep()
        setupBarChart()
        drawBarChart()
        setAverages()
    }

    private fun setupBarChart() {

        // define bar chart
        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        // define legend
        val legend: Legend = barChart.legend
        legend.textSize = 14f

        // define x axis
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f
        xAxis.labelCount = 5
        val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
        xAxis.valueFormatter = object : ValueFormatter() {

            override fun getFormattedValue(value: Float): String { // used by system

                val index = value.toInt()
                return if (index in 0 until sleepDataGlobal.size) {
                    sleepDataGlobal[index].date.format(formatter)
                } else {
                    Log.e(TAG, "Index out of bounds of sleep variable!")
                    ""
                }
            }
        }

        // define y axis
        val leftAxis = barChart.axisLeft
        leftAxis.textSize = 12f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 15f

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false
    }

    private fun drawBarChart() {

        // create entries and map y-values to x-values
        val sleepEntries: MutableList<BarEntry> = ArrayList()
        val totalSleepEntries: MutableList<BarEntry> = ArrayList()

        for (i in 0 until sleepDataGlobal.size) {
            val sleepYValue = sleepDataGlobal[i].hoursSlept
            val totalSleepYValue = sleepDataGlobal[i].extraHoursSlept + sleepDataGlobal[i].hoursSlept // sleep plus naps
            val sleepBarEntry = BarEntry(i.toFloat(), sleepYValue)
            val totalSleepBarEntry = BarEntry(i.toFloat(), totalSleepYValue)
            sleepEntries.add(sleepBarEntry)
            totalSleepEntries.add(totalSleepBarEntry)
        }

        // create datasets for sleep and total sleep
        val sleepDataSet = BarDataSet(sleepEntries, "Schlaf")
        sleepDataSet.valueTextSize = 12f
        sleepDataSet.color = Color.BLUE

        val totalSleepDataSet = BarDataSet(totalSleepEntries, "Naps")
        totalSleepDataSet.valueTextSize = 12f
        totalSleepDataSet.color = Color.GREEN

        val dataSets: MutableList<IBarDataSet> = mutableListOf(
            totalSleepDataSet,
            sleepDataSet
        ) // total sleep before sleep so that sleep bars overlap total sleep bars -> extra sleep visible

        // pass data to chart
        val data = BarData(dataSets)
        barChart.data = data

        // refresh chart
        barChart.invalidate()

        // show max of five bars, rest is scrollable
        barChart.setVisibleXRangeMaximum(5f)
    }

    private fun setAverages() {

        // calculate average sleep without naps
        var totalNightlySleep = 0F
        for (i in 0 until sleepDataGlobal.size) {
            totalNightlySleep += sleepDataGlobal[i].hoursSlept
        }
        var averageNightlySleep = totalNightlySleep / sleepDataGlobal.size

        // write average into textview
        binding.averageValue.text = averageNightlySleep.round(1).toString()
    }

    private fun getSleep() {

        val currentDate = LocalDate.now()
        val tempDate = startDate
        val sleepDataLocal = readFromDB()

        if (sleepDataLocal.isEmpty()) {
            sleepDataGlobal = sleepDataLocal
        } else {
            while (tempDate.isBefore(currentDate)) {
                sleepDataLocal.add(Sleep(tempDate, 8F, 0F, true, true, false))
                tempDate.plusDays(1)
            }
            sleepDataGlobal = sleepDataLocal
            writeToDB(sleepDataGlobal)
        }
    }

    private fun writeToDB(sleep: MutableList<Sleep>) {

        ;
    }

    private fun readFromDB() : MutableList<Sleep> {

        return ...
    }

    private fun Float.round(decimals: Int): Float {

        val multiplier = 10.0.pow(decimals)
        return (this * multiplier).roundToInt() / multiplier.toFloat()
    }
}
