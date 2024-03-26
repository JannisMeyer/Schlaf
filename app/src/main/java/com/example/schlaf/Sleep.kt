package com.example.schlaf

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "sleep_data")
data class Sleep (
    @PrimaryKey(autoGenerate = false) var id: Int,
    var day : Int,
    var month : Int,
    var year : Int,
    var hoursSlept : Float,
    var extraHoursSlept : Float,
    var feelingAwake : Boolean,
    var windowOpen : Boolean,
    var feelingSick : Boolean
) {
    constructor(date: LocalDate, hoursSlept: Float, extraHoursSlept: Float, feelingAwake: Boolean, windowOpen: Boolean, feelingSick: Boolean) : this(
        generateDateID(date), // generate id from date parameter
        date.dayOfMonth, // get year data from date parameter
        date.monthValue,
        date.year,
        hoursSlept,
        extraHoursSlept,
        feelingAwake,
        windowOpen,
        feelingSick
    )

    companion object {
        private fun generateDateID(date: LocalDate): Int {

            val formatter = DateTimeFormatter.ofPattern("yyyyMMdd")
            val dateIDString = date.format(formatter)
            return dateIDString.toInt()
        }
    }

    fun getDate() : LocalDate { // getter for date

        return LocalDate.of(year, month, day)
    }
}