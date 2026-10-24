package com.example.weekendx.data.model

import java.time.DayOfWeek
import java.time.LocalDate

data class WeekendXResult(
    val startDate: LocalDate,
    val endDate: LocalDate,
    val normalizedStart: LocalDate,
    val normalizedEnd: LocalDate,
    val isReversed: Boolean,
    val weekendDates: List<LocalDate>,
    val weekendGroups: List<WeekendGroup>,
    val completeWeekends: Int,
    val partialWeekends: Int,
    val calendarDays: Long,
    val weekendPercentage: Double,
    val weekendDays: Set<DayOfWeek>,
    val inclusive: Boolean,
    val dayOfWeekBreakdown: Map<DayOfWeek, List<LocalDate>>
)
