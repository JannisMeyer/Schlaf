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
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import kotlinx.coroutines.*
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import kotlin.math.pow
import kotlin.math.roundToInt

class MainActivity : AppCompatActivity() {

    // TODO: add comments
    // TODO: add persistence of data
    // TODO: add averages below graph and to graph
    // TODO: add modify functionality to bars
    // TODO: add today's bar functionality

    private lateinit var binding: ActivityMainBinding

    private lateinit var barChart: BarChart

    companion object {

        private lateinit var sleepDataGlobal : List<Sleep>
        private lateinit var startDate : LocalDate
        private var dataReadingCompleted = false
        private var dataWritingCompleted = false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect this activity with corresponding display
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // bind bar chart
        barChart = binding.barChart

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
        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(entry: Entry?, h: Highlight?) {
                if (entry != null) {
                    val barEntry = entry as BarEntry
                    val additionalData : Sleep = barEntry.data as Sleep // !! click selects totalSleepEntries (rear bars)
                    Log.i(TAG, "Awake after getting up: ${additionalData.feelingAwake}")
                    Log.i(TAG, "Window open: ${additionalData.windowOpen}")
                    Log.i(TAG, "Feeling sick: ${additionalData.feelingSick}")

                    binding.date.text = additionalData.getGermanDate()

                    val sleepString = "Schlaf: "+additionalData.hoursSlept.toString()
                    binding.sleep.text = sleepString

                    val extraSleepString = "Nap: "+additionalData.extraHoursSlept.toString()
                    binding.extraSleep.text = extraSleepString

                    val awakeString = if (additionalData.feelingAwake) {
                        "Wach: Ja"
                    } else {
                        "Wach: Nein"
                    }
                    binding.awake.text = awakeString

                    val windowString = if (additionalData.windowOpen) {
                        "Fenster: auf"
                    } else {
                        "Fenster: zu"
                    }
                    binding.window.text = windowString

                    val sickString = if (additionalData.feelingSick) {
                        "Krank: nein"
                    } else {
                        "Krank: ja"
                    }
                    binding.sick.text = sickString
                }
            }

            override fun onNothingSelected() {

                binding.date.text = ""
                binding.sleep.text = "Schlaf:"
                binding.extraSleep.text = "Nap:"
                binding.awake.text = "Wach:"
                binding.window.text = "Fenster:"
                binding.sick.text = "..."
            }
        })

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
                return if (index in sleepDataGlobal.indices) {
                    sleepDataGlobal[index].getRawDate().format(formatter)
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

        Log.d(TAG, "Setup of chart done")
    }

    private fun drawBarChart() {

        // create entries and map y-values to x-values
        val sleepEntries: MutableList<BarEntry> = ArrayList()
        val totalSleepEntries: MutableList<BarEntry> = ArrayList()

        for (i in sleepDataGlobal.indices) {
            val sleepYValue = sleepDataGlobal[i].hoursSlept
            val totalSleepYValue = sleepDataGlobal[i].extraHoursSlept + sleepDataGlobal[i].hoursSlept // sleep plus naps
            val sleepBarEntry = BarEntry(i.toFloat(), sleepYValue, sleepDataGlobal[i]) // add whole object as additional data
            val totalSleepBarEntry = BarEntry(i.toFloat(), totalSleepYValue, sleepDataGlobal[i])
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

        Log.d(TAG, "Drew bar chart")
    }

    private fun setAverages() {

        // calculate average sleep without naps
        var totalNightlySleep = 0F
        for (element in sleepDataGlobal) {
            totalNightlySleep += element.hoursSlept
        }
        val averageNightlySleep = totalNightlySleep / sleepDataGlobal.size

        // write average into textview
        binding.averageValue.text = averageNightlySleep.round(1).toString()
    }

    private fun getSleep() {

        val currentDate = LocalDate.now()
        Log.d(TAG, "Current date: $currentDate")
        //val tempDate = startDate
        var tempDate = LocalDate.of(2024, 3, 4)
        readFromDB()

        if (sleepDataGlobal.isEmpty()) { // DB was empty
            Log.d(TAG, "DB is empty")
            var sleepDataTemp : MutableList<Sleep> = mutableListOf()
            while (tempDate.isBefore(currentDate)) {
                Log.d(TAG, "temp date: $tempDate")
                sleepDataTemp.add(Sleep(tempDate, 8F, 1F, true, true, false))
                tempDate = tempDate.plusDays(1)
            }
            sleepDataGlobal = sleepDataTemp
            writeToDB(sleepDataGlobal)
            readFromDB()
        }
    }

    private fun writeToDB(sleepList: List<Sleep> = emptyList(), sleepEntry : Sleep? = null) {

        Log.d(TAG, "Entered writeToDB")
        CoroutineScope(Dispatchers.IO).launch {

            if (sleepList.isEmpty() && sleepEntry != null) { // passing of list or single entry possible
                DatabaseProvider.getDatabase(this@MainActivity).sleepDataDao().insertSleepData(sleepEntry)
                dataWritingCompleted = true
                Log.d(TAG, "Wrote sleep entry")
            } else if (sleepList.isNotEmpty() && sleepEntry == null) {
                Log.d(TAG, "Writing sleep list...")
                for (item in sleepList) {
                    DatabaseProvider.getDatabase(this@MainActivity).sleepDataDao().insertSleepData(item)
                }
                dataWritingCompleted = true
                Log.d(TAG, "Wrote sleep list")
            } else {
                Log.e(TAG, "Invalid input, data was not written to DB!")
                dataWritingCompleted = true
            }
        }

        while (!dataWritingCompleted) { // wait for coroutine to finish, function is synchronous this way
            ;
        }
        Log.d(TAG, "Data writing complete")
        dataWritingCompleted = false
    }

    private fun readFromDB() {

        CoroutineScope(Dispatchers.IO).launch {
            sleepDataGlobal = DatabaseProvider.getDatabase(this@MainActivity).sleepDataDao().getAllSleepData()
            dataReadingCompleted = true
        }

        while (!dataReadingCompleted) { // wait for coroutine to finish, function is synchronous this way
            ;
        }
        Log.d(TAG, "Data reading complete")
        dataReadingCompleted = false
    }

    private fun Float.round(decimals: Int): Float {

        val multiplier = 10.0.pow(decimals)
        return (this * multiplier).roundToInt() / multiplier.toFloat()
    }
}
