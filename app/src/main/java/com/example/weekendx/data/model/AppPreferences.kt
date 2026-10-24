package com.example.weekendx.data.model

import java.time.DayOfWeek

enum class ThemeMode {
    SYSTEM,
    LIGHT,
    DARK
}

enum class WeekStartOption {
    SYSTEM,
    MONDAY,
    SUNDAY
}

enum class DateFormatOption(val patternName: String) {
    DAY_MONTH_YEAR("24 October 2026"),
    MONTH_DAY_YEAR("Oct 24, 2026"),
    SLASH_DMY("24/10/2026"),
    ISO("2026-10-24")
}

enum class WeekendViewMode {
    GROUPED,
    COMPACT
}

data class WeekendSettingsData(
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val dynamicColor: Boolean = true,
    val weekendDays: Set<DayOfWeek> = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY),
    val inclusive: Boolean = true,
    val signedDifference: Boolean = false,
    val liveCalculation: Boolean = true,
    val calendarPreview: Boolean = true,
    val weekendPercentage: Boolean = true,
    val progress: Boolean = true,
    val monthlySummary: Boolean = false,
    val yearlySummary: Boolean = false,
    val weekNumbers: Boolean = false,
    val weekStart: WeekStartOption = WeekStartOption.SYSTEM,
    val dateFormat: DateFormatOption = DateFormatOption.DAY_MONTH_YEAR,
    val hapticFeedback: Boolean = true,
    val animations: Boolean = true,
    val confirmDelete: Boolean = true,
    val restoreLastDates: Boolean = true,
    val weekendViewMode: WeekendViewMode = WeekendViewMode.GROUPED
)
