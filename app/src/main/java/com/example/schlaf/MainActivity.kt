package com.example.schlaf

import android.graphics.Color
import android.os.Bundle
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
import java.math.BigDecimal
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // TODO: add comments
    // TODO: add persistence of data
    // TODO: add averages below graph and to graph
    // TODO: add modify functionality to bars
    // TODO: move average into own function, globalize dataset size

    private lateinit var binding: ActivityMainBinding


    private lateinit var barChart: BarChart

    // define variables for manually tracked sleep
    private var DAY = 19
    private var MONTH = 3
    private var manuallyTrackedSleep : MutableList<Sleep> = mutableListOf(
        Sleep(12F, 0F, true, false, true),
        Sleep(6F, 2F, true, false, true),
        Sleep(7.5F, 0F, true, true, true),
        Sleep(11F, 0F, false, true, true),
        Sleep(11F, 0F, true, true, false),
        Sleep(6F, 1.25F, false, true, false),
        Sleep(10F, 0F, true, true, false),
        Sleep(10F, 0F, true, true, false),
        Sleep(11F, 0F, false, true, false),
        Sleep(11.5F, 1F, false, true, false),
        Sleep(8F, 0F, false, true, false),
        Sleep(10.5F, 0F, true, false, false),
        Sleep(10F, 0F, true, true, false),
        Sleep(8F, 0F, true, true, false),
        Sleep(7.5F, 0F, false, true, false),
        Sleep(7.5F, 0F, true, true, false))

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect this activity with corresponding display
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBarChart()
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

                // create date data for y axis
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val startDate = LocalDate.of(2024, 3, 4)
                val endDate = LocalDate.of(2024, MONTH, DAY)
                var tempDate = startDate
                while (!tempDate.isAfter(endDate)) {
                    labels.add(tempDate.format(formatter))
                    tempDate = tempDate.plusDays(1)
                }

                addCurrentDate()
            }

            fun addCurrentDate() {

                val currentDate = LocalDate.now()
                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val formattedDate = currentDate.format(formatter)
                labels.add(formattedDate)
            }

            override fun getFormattedValue(value: Float): String {

                val index = value.toInt()
                return if (index >= 0 && index < labels.size) {
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

        for (i in 0 until labels.size - 1) {
            val sleepYValue = manuallyTrackedSleep[i].hoursSlept
            val totalSleepYValue = manuallyTrackedSleep[i].extraHoursSlept + manuallyTrackedSleep[i].hoursSlept // sleep plus naps
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

        val dataSets : MutableList<IBarDataSet> = mutableListOf(totalSleepDataSet, sleepDataSet) // total sleep before sleep so that sleep bars overlap total sleep bars -> extra sleep visible

        // pass data to chart
        val data = BarData(dataSets)
        barChart.data = data

        // refresh chart
        barChart.invalidate()

        // show max of five bars, rest is scrollable
        barChart.setVisibleXRangeMaximum(5f)

        // calculate average sleep
        var totalNightlySleep : Float = 0F
        for (i in 0 until labels.size - 1) {
            totalNightlySleep += manuallyTrackedSleep[i].hoursSlept
        }
        var averageNightlySleep = totalNightlySleep / (labels.size - 1)
        binding.averageValue.text = averageNightlySleep.round(1).toString()
    }

    private fun Float.round(decimals: Int): Float {

        val multiplier = 10.0.pow(decimals)
        return (this * multiplier).roundToInt() / multiplier.toFloat()
    }
}
