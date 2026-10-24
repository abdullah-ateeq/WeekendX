package com.example.weekendx.data.model

import java.time.LocalDate
import java.time.YearMonth

data class MonthlySummary(
    val yearMonth: YearMonth,
    val weekendDays: Int,
    val completeWeekends: Int,
    val partialWeekends: Int,
    val calendarDays: Int,
    val weekendDates: List<LocalDate>
)
