package com.example.weekendx.domain.calendar

import com.example.weekendx.data.model.CalendarDay
import com.example.weekendx.data.model.CalendarMonthData
import com.example.weekendx.data.model.WeekStartOption
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.WeekFields
import java.util.Locale

class CalendarVisualizer {

    fun getFirstDayOfWeek(weekStartOption: WeekStartOption): DayOfWeek {
        return when (weekStartOption) {
            WeekStartOption.MONDAY -> DayOfWeek.MONDAY
            WeekStartOption.SUNDAY -> DayOfWeek.SUNDAY
            WeekStartOption.SYSTEM -> WeekFields.of(Locale.getDefault()).firstDayOfWeek
        }
    }

    fun getWeekdayHeaders(firstDayOfWeek: DayOfWeek): List<DayOfWeek> {
        val days = DayOfWeek.values()
        val startIndex = days.indexOf(firstDayOfWeek)
        return (0 until 7).map { days[(startIndex + it) % 7] }
    }

    fun generateMonthData(
        yearMonth: YearMonth,
        firstDayOfWeek: DayOfWeek,
        weekendDays: Set<DayOfWeek>,
        normalizedStart: LocalDate,
        normalizedEnd: LocalDate,
        inclusive: Boolean
    ): CalendarMonthData {
        val weekdayHeaders = getWeekdayHeaders(firstDayOfWeek)
        val today = LocalDate.now()

        val lengthOfMonth = yearMonth.lengthOfMonth()
        val firstDateOfMonth = yearMonth.atDay(1)

        val firstDow = firstDateOfMonth.dayOfWeek
        val paddingDays = (firstDow.value - firstDayOfWeek.value + 7) % 7

        val weeks = mutableListOf<List<CalendarDay?>>()
        var currentWeek = mutableListOf<CalendarDay?>()

        // Pad preceding days
        for (i in 0 until paddingDays) {
            currentWeek.add(null)
        }

        for (dayNumber in 1..lengthOfMonth) {
            val date = yearMonth.atDay(dayNumber)
            val isWeekend = date.dayOfWeek in weekendDays

            val isInRange = if (inclusive) {
                !date.isBefore(normalizedStart) && !date.isAfter(normalizedEnd)
            } else {
                date.isAfter(normalizedStart) && date.isBefore(normalizedEnd)
            }

            val isStart = date == normalizedStart
            val isEnd = date == normalizedEnd
            val isToday = date == today

            currentWeek.add(
                CalendarDay(
                    date = date,
                    isWeekend = isWeekend,
                    isInRange = isInRange,
                    isStart = isStart,
                    isEnd = isEnd,
                    isToday = isToday
                )
            )

            if (currentWeek.size == 7) {
                weeks.add(currentWeek)
                currentWeek = mutableListOf()
            }
        }

        if (currentWeek.isNotEmpty()) {
            while (currentWeek.size < 7) {
                currentWeek.add(null)
            }
            weeks.add(currentWeek)
        }

        return CalendarMonthData(
            yearMonth = yearMonth,
            weekdayHeaders = weekdayHeaders,
            weeks = weeks
        )
    }
}
