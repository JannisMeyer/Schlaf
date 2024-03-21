package com.example.schlaf

import android.content.ContentValues
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


    private lateinit var barChart: BarChart

    // define variables for manually tracked sleep
    companion object {

        private var manuallyTrackedSleep : MutableList<Sleep> = mutableListOf(
            Sleep(LocalDate.of(2024,3,4),12F, 0F, true, false, true),
            Sleep(LocalDate.of(2024,3,5),6F, 2F, true, false, true),
            Sleep(LocalDate.of(2024,3,6),7.5F, 0F, true, true, true),
            Sleep(LocalDate.of(2024,3,7),11F, 0F, false, true, true),
            Sleep(LocalDate.of(2024,3,8),11F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,9),6F, 1.25F, false, true, false),
            Sleep(LocalDate.of(2024,3,10),10F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,11),10F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,12),11F, 0F, false, true, false),
            Sleep(LocalDate.of(2024,3,13),11.5F, 1F, false, true, false),
            Sleep(LocalDate.of(2024,3,14),8F, 0F, false, true, false),
            Sleep(LocalDate.of(2024,3,15),10.5F, 0F, true, false, false),
            Sleep(LocalDate.of(2024,3,16),10F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,17),8F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,18),7.5F, 0F, false, true, false),
            Sleep(LocalDate.of(2024,3,19),7.5F, 0F, true, true, false),
            Sleep(LocalDate.of(2024,3,20),10F, 0F, true, true, false))
        private var dataSetSize = manuallyTrackedSleep.size
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect this activity with corresponding display
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBarChart()
        setAverages()

        //Log.d(TAG, dataSetSize.toString())
    }

    private fun setupBarChart() {

        // define bar chart
        barChart = binding.barChart

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        // define legend
        val legend: Legend = barChart.legend
        //legend.isEnabled = false
        legend.textSize = 14f

        // define x axis
        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f
        xAxis.labelCount = 5
        val labels: MutableList<String> = mutableListOf()
        xAxis.valueFormatter = object : ValueFormatter() {
            init {

                // get date data for y axis
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                for (i in 0 until dataSetSize) {
                    labels.add(manuallyTrackedSleep[i].date.format(formatter))
                }

                //addCurrentDate()
            }

            fun addCurrentDate() {

                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val formattedDate = currentDate.format(formatter)
                labels.add(formattedDate)
            }

            override fun getFormattedValue(value: Float): String { // currently not used

                val index = value.toInt()
                return if (index in 0 until dataSetSize) {
                    labels[index]
                } else {
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

        // create entries and map y-values to x-values
        val sleepEntries: MutableList<BarEntry> = ArrayList()
        val totalSleepEntries: MutableList<BarEntry> = ArrayList()

        for (i in 0 until dataSetSize) {
            val sleepYValue = manuallyTrackedSleep[i].hoursSlept
            val totalSleepYValue = manuallyTrackedSleep[i].extraHoursSlept + manuallyTrackedSleep[i].hoursSlept // sleep plus naps
            val sleepBarEntry = BarEntry(i.toFloat(), sleepYValue)
            val totalSleepBarEntry = BarEntry(i.toFloat(), totalSleepYValue)
            sleepEntries.add(sleepBarEntry)
            totalSleepEntries.add(totalSleepBarEntry)

            //Log.d(TAG, sleepYValue.toString())
        }

        // create datasets for sleep and total sleep
        val sleepDataSet = BarDataSet(sleepEntries, "Schlaf")
        sleepDataSet.valueTextSize = 12f
        sleepDataSet.color = Color.BLUE

        val totalSleepDataSet = BarDataSet(totalSleepEntries, "Naps")
        totalSleepDataSet.valueTextSize = 12f
        totalSleepDataSet.color = Color.GREEN

        val dataSets : MutableList<IBarDataSet> = mutableListOf(totalSleepDataSet, sleepDataSet) // total sleep before sleep so that sleep bars overlap total sleep bars -> extra sleep visible

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
        for (i in 0 until dataSetSize) {
            totalNightlySleep += manuallyTrackedSleep[i].hoursSlept
        }
        var averageNightlySleep = totalNightlySleep / dataSetSize

        // write average into textview
        binding.averageValue.text = averageNightlySleep.round(1).toString()
    }

    private fun Float.round(decimals: Int): Float {

        val multiplier = 10.0.pow(decimals)
        return (this * multiplier).roundToInt() / multiplier.toFloat()
    }
}
