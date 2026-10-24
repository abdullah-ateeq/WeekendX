package com.example.weekendx.ui.home

import com.example.weekendx.data.model.CalendarMonthData
import com.example.weekendx.data.model.DateDetails
import com.example.weekendx.data.model.MonthlySummary
import com.example.weekendx.data.model.WeekendSettingsData
import com.example.weekendx.data.model.WeekendXHistory
import com.example.weekendx.data.model.WeekendXResult
import com.example.weekendx.data.model.YearlySummary
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth

data class WeekendXUiState(
    val startDate: LocalDate = LocalDate.now(),
    val endDate: LocalDate = LocalDate.now(),
    val weekendDays: Set<DayOfWeek> = setOf(
        DayOfWeek.SATURDAY,
        DayOfWeek.SUNDAY
    ),
    val inclusive: Boolean = true,
    val result: WeekendXResult? = null,
    val isCalculating: Boolean = false,
    val errorMessage: String? = null,
    val history: List<WeekendXHistory> = emptyList(),
    val settings: WeekendSettingsData = WeekendSettingsData(),
    val previewMonth: YearMonth = YearMonth.now(),
    val monthlySummaries: List<MonthlySummary> = emptyList(),
    val yearlySummaries: List<YearlySummary> = emptyList(),
    val calendarMonthData: CalendarMonthData? = null,
    val selectedDateDetails: DateDetails? = null,
    val infoMessage: String? = null
)
