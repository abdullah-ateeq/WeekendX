package com.example.weekendx.data.model

import java.time.LocalDate

data class DateDetails(
    val date: LocalDate,
    val isWeekend: Boolean,
    val dayOfYear: Int,
    val totalDaysInYear: Int,
    val weekNumber: Int,
    val isLeapYear: Boolean,
    val customWeekendNote: String
)
