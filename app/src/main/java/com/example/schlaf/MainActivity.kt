package com.example.schlaf

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.schlaf.databinding.ActivityMainBinding
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    // TODO: add proper y values
    // TODO: add comments
    // TODO: add persistence of data
    // TODO: add averages below graph
    // TODO: add modify functionality to bars

    private lateinit var barChart: BarChart

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // connect this activity with corresponding display
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBarChart()
    }

    private fun setupBarChart() {
        barChart = binding.barChart

        barChart.setDrawBarShadow(false)
        barChart.setDrawValueAboveBar(true)
        barChart.description.isEnabled = false
        barChart.setPinchZoom(false)
        barChart.setDrawGridBackground(false)
        barChart.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY

        val legend: Legend = barChart.legend
        legend.isEnabled = false
        legend.textSize = 12f

        val xAxis: XAxis = barChart.xAxis
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.setDrawGridLines(false)
        xAxis.textSize = 12f
        xAxis.labelCount = 5
        val labels: MutableList<String> = mutableListOf()
        xAxis.valueFormatter = object : ValueFormatter() {
            init {

                val formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
                val startDate = LocalDate.of(2024, 3, 4)
                val endDate = LocalDate.of(2024, 3, 19)
                var currentDate = startDate
                while (!currentDate.isAfter(endDate)) {
                    labels.add(currentDate.format(formatter))
                    currentDate = currentDate.plusDays(1)
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

        val leftAxis = barChart.axisLeft
        leftAxis.textSize = 12f
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 15f

        val rightAxis = barChart.axisRight
        rightAxis.isEnabled = false

        // add some initial entries/bars
        val entries: MutableList<BarEntry> = ArrayList()

        for (i in 0 until labels.size) {
            val yValue = 8f
            val barEntry = BarEntry(i.toFloat(), yValue)
            entries.add(barEntry)
        }

        val dataSet = BarDataSet(entries, "Label")
        dataSet.valueTextSize = 12f

        val data = BarData(dataSet)
        barChart.data = data

        // refresh chart
        barChart.invalidate()

        // show max of six bars, rest is scrollable
        barChart.setVisibleXRangeMaximum(5f)

    }
}
