package com.example.weekendx

import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.domain.calculator.WeekendCalculator
import com.example.weekendx.domain.formatter.WeekendFormatter
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeekendFormatterTest {

    private lateinit var formatter: WeekendFormatter
    private lateinit var calculator: WeekendCalculator

    @Before
    fun setUp() {
        formatter = WeekendFormatter()
        calculator = WeekendCalculator()
    }

    @Test
    fun testDateFormatting() {
        val date = LocalDate.of(2026, 10, 24)

        assertEquals("24 October 2026", formatter.formatDate(date, DateFormatOption.DAY_MONTH_YEAR))
        assertEquals("Oct 24, 2026", formatter.formatDate(date, DateFormatOption.MONTH_DAY_YEAR))
        assertEquals("24/10/2026", formatter.formatDate(date, DateFormatOption.SLASH_DMY))
        assertEquals("2026-10-24", formatter.formatDate(date, DateFormatOption.ISO))
    }

    @Test
    fun testFormatFullResults() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val result = calculator.calculate(start, end, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), inclusive = true)

        val text = formatter.formatFullResults(result, DateFormatOption.DAY_MONTH_YEAR)

        assertTrue(text.contains("WeekendX"))
        assertTrue(text.contains("1 October 2026 → 31 October 2026"))
        assertTrue(text.contains("Weekend days:\n9"))
        assertTrue(text.contains("Complete weekends:\n4"))
        assertTrue(text.contains("Partial weekends:\n1"))
        assertTrue(text.contains("29.0%"))
        assertTrue(text.contains("Saturday, 3 October 2026"))
        assertTrue(text.contains("Sunday, 4 October 2026"))
        assertTrue(text.contains("Saturday, 31 October 2026"))
    }

    @Test
    fun testFormatDatesOnly() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val result = calculator.calculate(start, end, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), inclusive = true)

        val text = formatter.formatDatesOnly(result, DateFormatOption.DAY_MONTH_YEAR)

        val lines = text.lines()
        assertEquals(9, lines.size)
        assertEquals("3 October 2026", lines[0])
        assertEquals("4 October 2026", lines[1])
        assertEquals("31 October 2026", lines[8])
    }

    @Test
    fun testDateDetails() {
        val date = LocalDate.of(2026, 10, 24) // Saturday
        val details = formatter.getDateDetails(date, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), DayOfWeek.MONDAY)

        assertTrue(details.isWeekend)
        assertEquals(297, details.dayOfYear)
        assertEquals(365, details.totalDaysInYear)
        assertEquals(43, details.weekNumber)
        assertEquals(false, details.isLeapYear)
        assertEquals("Weekend", details.customWeekendNote)
    }
}
