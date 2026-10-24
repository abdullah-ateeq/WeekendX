package com.example.weekendx.data.model

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class CalendarDay(
    val date: LocalDate,
    val isWeekend: Boolean,
    val isInRange: Boolean,
    val isStart: Boolean,
    val isEnd: Boolean,
    val isToday: Boolean
)

data class CalendarMonthData(
    val yearMonth: YearMonth,
    val weekdayHeaders: List<DayOfWeek>,
    val weeks: List<List<CalendarDay?>>
)
