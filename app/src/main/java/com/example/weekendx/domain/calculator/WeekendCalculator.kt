package com.example.weekendx.domain.calculator

import com.example.weekendx.data.model.WeekendGroup
import com.example.weekendx.data.model.WeekendXResult
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale

class WeekendCalculator {

    /**
     * Finds every weekend date within the range and computes groups and statistics.
     */
    fun calculate(
        startDate: LocalDate,
        endDate: LocalDate,
        weekendDays: Set<DayOfWeek>,
        inclusive: Boolean
    ): WeekendXResult {
        val safeWeekendDays = if (weekendDays.isEmpty()) {
            setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        } else {
            weekendDays
        }

        val isReversed = startDate.isAfter(endDate)
        val normalizedStart = if (isReversed) endDate else startDate
        val normalizedEnd = if (isReversed) startDate else endDate

        // Determine inspection bounds
        val rangeStart: LocalDate?
        val rangeEnd: LocalDate?

        val calendarDays: Long

        if (inclusive) {
            rangeStart = normalizedStart
            rangeEnd = normalizedEnd
            calendarDays = ChronoUnit.DAYS.between(normalizedStart, normalizedEnd) + 1L
        } else {
            calendarDays = ChronoUnit.DAYS.between(normalizedStart, normalizedEnd)
            if (normalizedStart == normalizedEnd || normalizedStart.plusDays(1L).isAfter(normalizedEnd.minusDays(1L))) {
                rangeStart = null
                rangeEnd = null
            } else {
                rangeStart = normalizedStart.plusDays(1L)
                rangeEnd = normalizedEnd.minusDays(1L)
            }
        }

        // Find all matching weekend dates
        val weekendDates = mutableListOf<LocalDate>()
        if (rangeStart != null && rangeEnd != null) {
            var curr: LocalDate = rangeStart
            while (!curr.isAfter(rangeEnd)) {
                if (curr.dayOfWeek in safeWeekendDays) {
                    weekendDates.add(curr)
                }
                curr = curr.plusDays(1L)
            }
        }

        // Weekend Grouping
        val weekendGroups = groupWeekends(
            weekendDates = weekendDates,
            rangeStart = rangeStart,
            rangeEnd = rangeEnd,
            weekendDays = safeWeekendDays
        )

        val completeWeekends = weekendGroups.count { it.isComplete }
        val partialWeekends = weekendGroups.count { !it.isComplete }

        val weekendPercentage = if (calendarDays > 0) {
            (weekendDates.size.toDouble() / calendarDays.toDouble()) * 100.0
        } else {
            0.0
        }

        // Day of week breakdown
        val breakdown = safeWeekendDays.sorted().associateWith { dow ->
            weekendDates.filter { it.dayOfWeek == dow }
        }

        return WeekendXResult(
            startDate = startDate,
            endDate = endDate,
            normalizedStart = normalizedStart,
            normalizedEnd = normalizedEnd,
            isReversed = isReversed,
            weekendDates = weekendDates,
            weekendGroups = weekendGroups,
            completeWeekends = completeWeekends,
            partialWeekends = partialWeekends,
            calendarDays = calendarDays,
            weekendPercentage = weekendPercentage,
            weekendDays = safeWeekendDays,
            inclusive = inclusive,
            dayOfWeekBreakdown = breakdown
        )
    }

    /**
     * Groups dates by calendar weekend cycle.
     */
    private fun groupWeekends(
        weekendDates: List<LocalDate>,
        rangeStart: LocalDate?,
        rangeEnd: LocalDate?,
        weekendDays: Set<DayOfWeek>
    ): List<WeekendGroup> {
        if (weekendDates.isEmpty() || rangeStart == null || rangeEnd == null) {
            return emptyList()
        }

        // Determine cycle anchor:
        // If Sunday and Monday are configured as weekend, but not Saturday, week cycle starts on Sunday.
        val cycleStartDow = if (
            weekendDays.contains(DayOfWeek.SUNDAY) &&
            weekendDays.contains(DayOfWeek.MONDAY) &&
            !weekendDays.contains(DayOfWeek.SATURDAY)
        ) {
            DayOfWeek.SUNDAY
        } else {
            DayOfWeek.MONDAY
        }

        // Map each weekend date to its cycle start date
        val groupsMap = linkedMapOf<LocalDate, MutableList<LocalDate>>()
        for (date in weekendDates) {
            val cycleStart = date.with(TemporalAdjusters.previousOrSame(cycleStartDow))
            groupsMap.getOrPut(cycleStart) { mutableListOf() }.add(date)
        }

        var groupIndex = 1
        return groupsMap.map { (cycleStart, datesInCycle) ->
            // In this 7-day cycle, determine all configured weekend dates
            val allConfiguredDatesInCycle = (0L until 7L).map { cycleStart.plusDays(it) }
                .filter { it.dayOfWeek in weekendDays }

            // Complete weekend if every configured weekend day of this cycle falls inside [rangeStart, rangeEnd]
            val isComplete = allConfiguredDatesInCycle.all { date ->
                !date.isBefore(rangeStart) && !date.isAfter(rangeEnd)
            }

            val label = if (isComplete) {
                "Weekend $groupIndex"
            } else {
                "Weekend $groupIndex (Partial)"
            }

            groupIndex++
            WeekendGroup(
                groupNumber = groupIndex - 1,
                dates = datesInCycle,
                isComplete = isComplete,
                label = label
            )
        }
    }
}
