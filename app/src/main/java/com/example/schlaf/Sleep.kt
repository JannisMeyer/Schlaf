package com.example.schlaf

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDate

@Entity(tableName = "sleepData")
data class Sleep (
    @PrimaryKey(autoGenerate = true) var date: LocalDate,
    var hoursSlept : Float = 8.0F,
    var extraHoursSlept : Float = 0.0F,
    var feelingAwake : Boolean = true,
    var windowOpen : Boolean = true,
    var feelingSick : Boolean = false
)