package com.example.schlaf

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Entity(tableName = "sleepData")
data class Sleep (
    @PrimaryKey(autoGenerate = false) var id: Int,
    var date : LocalDate,
    var hoursSlept : Float,
    var extraHoursSlept : Float,
    var feelingAwake : Boolean,
    var windowOpen : Boolean,
    var feelingSick : Boolean
) {
    constructor(date: LocalDate, hoursSlept: Float, extraHoursSlept: Float, feelingAwake: Boolean, windowOpen: Boolean, feelingSick: Boolean) : this(
        generateDateID(date), // generate id from date variable
        date,
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
}