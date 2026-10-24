package com.example.weekendx.domain.formatter

import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.data.model.DateDetails
import com.example.weekendx.data.model.WeekendXResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

class WeekendFormatter {

    fun formatDate(date: LocalDate, option: DateFormatOption): String {
        val pattern = when (option) {
            DateFormatOption.DAY_MONTH_YEAR -> "d MMMM yyyy"
            DateFormatOption.MONTH_DAY_YEAR -> "MMM d, yyyy"
            DateFormatOption.SLASH_DMY -> "dd/MM/yyyy"
            DateFormatOption.ISO -> "yyyy-MM-dd"
        }
        return date.format(DateTimeFormatter.ofPattern(pattern, Locale.getDefault()))
    }

    fun formatDateWithDay(date: LocalDate, option: DateFormatOption): String {
        val dayName = date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())
        return "$dayName, ${formatDate(date, option)}"
    }

    fun formatWeekendDaysLabel(weekendDays: Set<DayOfWeek>): String {
        return weekendDays.sorted().joinToString(" + ") {
            it.getDisplayName(TextStyle.FULL, Locale.getDefault())
        }
    }

    fun formatFullResults(result: WeekendXResult, dateFormat: DateFormatOption): String {
        val startFormatted = formatDate(result.startDate, dateFormat)
        val endFormatted = formatDate(result.endDate, dateFormat)
        val weekendLabel = formatWeekendDaysLabel(result.weekendDays)

        val sb = StringBuilder()
        sb.appendLine("WeekendX")
        sb.appendLine()
        sb.appendLine("Date range:")
        sb.appendLine("$startFormatted → $endFormatted")
        if (result.isReversed) {
            sb.appendLine("(Reversed Range)")
        }
        sb.appendLine()
        sb.appendLine("Weekend:")
        sb.appendLine(weekendLabel)
        sb.appendLine()
        sb.appendLine("Weekend days:")
        sb.appendLine(result.weekendDates.size)
        sb.appendLine()
        sb.appendLine("Complete weekends:")
        sb.appendLine(result.completeWeekends)
        sb.appendLine()
        sb.appendLine("Partial weekends:")
        sb.appendLine(result.partialWeekends)
        sb.appendLine()
        sb.appendLine("Weekend share:")
        sb.appendLine(String.format(Locale.US, "%.1f%%", result.weekendPercentage))
        sb.appendLine()
        sb.appendLine("Weekend dates:")
        sb.appendLine()

        if (result.weekendDates.isEmpty()) {
            sb.appendLine("No weekend dates found.")
        } else {
            result.weekendDates.forEach { date ->
                sb.appendLine(formatDateWithDay(date, dateFormat))
            }
        }

        return sb.toString().trimEnd()
    }

    fun formatDatesOnly(result: WeekendXResult, dateFormat: DateFormatOption): String {
        if (result.weekendDates.isEmpty()) {
            return "No weekend dates found."
        }
        return result.weekendDates.joinToString("\n") { date ->
            formatDate(date, dateFormat)
        }
    }

    fun getDateDetails(
        date: LocalDate,
        weekendDays: Set<DayOfWeek>,
        firstDayOfWeek: DayOfWeek
    ): DateDetails {
        val isWeekend = date.dayOfWeek in weekendDays
        val dayOfYear = date.dayOfYear
        val totalDaysInYear = date.lengthOfYear()
        val weekFields = WeekFields.of(firstDayOfWeek, 1)
        val weekNumber = date.get(weekFields.weekOfYear())
        val isLeapYear = date.isLeapYear

        val note = if (isWeekend) {
            if (weekendDays == setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)) {
                "Weekend"
            } else {
                "Configured weekend day"
            }
        } else {
            "Weekday (Non-weekend day)"
        }

        return DateDetails(
            date = date,
            isWeekend = isWeekend,
            dayOfYear = dayOfYear,
            totalDaysInYear = totalDaysInYear,
            weekNumber = weekNumber,
            isLeapYear = isLeapYear,
            customWeekendNote = note
        )
    }
}
