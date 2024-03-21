package com.example.schlaf

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "sleepData")
data class Sleep (
    @PrimaryKey(autoGenerate = false) var date: LocalDate,
    var hoursSlept : Float,
    var extraHoursSlept : Float,
    var feelingAwake : Boolean,
    var windowOpen : Boolean,
    var feelingSick : Boolean
)