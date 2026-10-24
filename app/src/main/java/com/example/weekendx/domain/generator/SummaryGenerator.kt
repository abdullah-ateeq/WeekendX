package com.example.weekendx.domain.generator

import com.example.weekendx.data.model.MonthlySummary
import com.example.weekendx.data.model.WeekendXResult
import com.example.weekendx.data.model.YearlySummary
import com.example.weekendx.domain.calculator.WeekendCalculator
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.ChronoUnit

class SummaryGenerator(
    private val calculator: WeekendCalculator = WeekendCalculator()
) {

    fun generateMonthlySummaries(result: WeekendXResult): List<MonthlySummary> {
        val startYm = YearMonth.from(result.normalizedStart)
        val endYm = YearMonth.from(result.normalizedEnd)

        val summaries = mutableListOf<MonthlySummary>()
        var currYm = startYm

        while (!currYm.isAfter(endYm)) {
            val monthStart = maxOf(result.normalizedStart, currYm.atDay(1))
            val monthEnd = minOf(result.normalizedEnd, currYm.atEndOfMonth())

            val subResult = calculator.calculate(
                startDate = monthStart,
                endDate = monthEnd,
                weekendDays = result.weekendDays,
                inclusive = result.inclusive
            )

            summaries.add(
                MonthlySummary(
                    yearMonth = currYm,
                    weekendDays = subResult.weekendDates.size,
                    completeWeekends = subResult.completeWeekends,
                    partialWeekends = subResult.partialWeekends,
                    calendarDays = subResult.calendarDays.toInt(),
                    weekendDates = subResult.weekendDates
                )
            )

            currYm = currYm.plusMonths(1L)
        }

        return summaries
    }

    fun generateYearlySummaries(result: WeekendXResult): List<YearlySummary> {
        val startYear = result.normalizedStart.year
        val endYear = result.normalizedEnd.year

        val summaries = mutableListOf<YearlySummary>()
        for (year in startYear..endYear) {
            val yearStart = maxOf(result.normalizedStart, LocalDate.of(year, 1, 1))
            val yearEnd = minOf(result.normalizedEnd, LocalDate.of(year, 12, 31))

            val subResult = calculator.calculate(
                startDate = yearStart,
                endDate = yearEnd,
                weekendDays = result.weekendDays,
                inclusive = result.inclusive
            )

            summaries.add(
                YearlySummary(
                    year = year,
                    weekendDays = subResult.weekendDates.size,
                    completeWeekends = subResult.completeWeekends,
                    partialWeekends = subResult.partialWeekends,
                    calendarDays = subResult.calendarDays.toInt()
                )
            )
        }

        return summaries
    }
}
