package com.example.weekendx

import com.example.weekendx.domain.calculator.WeekendCalculator
import java.time.DayOfWeek
import java.time.LocalDate
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class WeekendCalculatorTest {

    private lateinit var calculator: WeekendCalculator

    @Before
    fun setUp() {
        calculator = WeekendCalculator()
    }

    @Test
    fun testOctober2026() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(9, result.weekendDates.size)
        assertEquals(31L, result.calendarDays)
        assertEquals(5, result.weekendGroups.size)
        assertEquals(4, result.completeWeekends)
        assertEquals(1, result.partialWeekends)
        assertEquals(29.0, result.weekendPercentage, 0.1)

        val expectedDates = listOf(
            LocalDate.of(2026, 10, 3),
            LocalDate.of(2026, 10, 4),
            LocalDate.of(2026, 10, 10),
            LocalDate.of(2026, 10, 11),
            LocalDate.of(2026, 10, 17),
            LocalDate.of(2026, 10, 18),
            LocalDate.of(2026, 10, 24),
            LocalDate.of(2026, 10, 25),
            LocalDate.of(2026, 10, 31)
        )
        assertEquals(expectedDates, result.weekendDates)
    }

    @Test
    fun testNovember2026() {
        // November 2026 has 30 days. Nov 1 is Sunday.
        val start = LocalDate.of(2026, 11, 1)
        val end = LocalDate.of(2026, 11, 30)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        // Saturdays: 7, 14, 21, 28 (4). Sundays: 1, 8, 15, 22, 29 (5). Total = 9.
        assertEquals(9, result.weekendDates.size)
        assertEquals(30L, result.calendarDays)
    }

    @Test
    fun testFullWeek() {
        val start = LocalDate.of(2026, 10, 5) // Monday
        val end = LocalDate.of(2026, 10, 11)   // Sunday
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(2, result.weekendDates.size)
        assertEquals(1, result.completeWeekends)
        assertEquals(0, result.partialWeekends)
        assertEquals(1, result.weekendGroups.size)
    }

    @Test
    fun testSaturdayOnlyRange() {
        val start = LocalDate.of(2026, 10, 3)
        val end = LocalDate.of(2026, 10, 3)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(1, result.weekendDates.size)
        assertEquals(0, result.completeWeekends)
        assertEquals(1, result.partialWeekends)
    }

    @Test
    fun testSundayOnlyRange() {
        val start = LocalDate.of(2026, 10, 4)
        val end = LocalDate.of(2026, 10, 4)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(1, result.weekendDates.size)
        assertEquals(0, result.completeWeekends)
        assertEquals(1, result.partialWeekends)
    }

    @Test
    fun testWeekdayOnly() {
        val start = LocalDate.of(2026, 10, 5) // Mon
        val end = LocalDate.of(2026, 10, 9)   // Fri
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(0, result.weekendDates.size)
        assertEquals(0, result.completeWeekends)
        assertEquals(0, result.partialWeekends)
        assertEquals(5L, result.calendarDays)
    }

    @Test
    fun testReversedRange() {
        val start = LocalDate.of(2026, 10, 31)
        val end = LocalDate.of(2026, 10, 1)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertTrue(result.isReversed)
        assertEquals(9, result.weekendDates.size)
        assertEquals(31L, result.calendarDays)
    }

    @Test
    fun testExclusiveRange() {
        val start = LocalDate.of(2026, 10, 3) // Saturday
        val end = LocalDate.of(2026, 10, 4)   // Sunday
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val resultInclusive = calculator.calculate(start, end, weekend, inclusive = true)
        assertEquals(2, resultInclusive.weekendDates.size)

        val resultExclusive = calculator.calculate(start, end, weekend, inclusive = false)
        assertEquals(0, resultExclusive.weekendDates.size)
        assertEquals(1L, resultExclusive.calendarDays)
    }

    @Test
    fun testYearBoundary() {
        val start = LocalDate.of(2026, 12, 31) // Thursday
        val end = LocalDate.of(2027, 1, 3)     // Sunday
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        assertEquals(2, result.weekendDates.size)
        assertEquals(LocalDate.of(2027, 1, 2), result.weekendDates[0])
        assertEquals(LocalDate.of(2027, 1, 3), result.weekendDates[1])
        assertEquals(1, result.completeWeekends)
    }

    @Test
    fun testLeapYear() {
        val start = LocalDate.of(2024, 2, 27) // Tuesday
        val end = LocalDate.of(2024, 3, 3)     // Sunday
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        // 2024 is leap year, 29 Feb is Thursday
        assertTrue(LocalDate.of(2024, 2, 29).isLeapYear)
        assertEquals(6L, result.calendarDays) // 27, 28, 29 Feb, 1, 2, 3 Mar = 6 days
        assertEquals(2, result.weekendDates.size) // 2 Mar (Sat), 3 Mar (Sun)
        assertEquals(1, result.completeWeekends)
    }

    @Test
    fun testCustomWeekendFridaySaturday() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val weekend = setOf(DayOfWeek.FRIDAY, DayOfWeek.SATURDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        // October 2026 has 5 Fridays (2, 9, 16, 23, 30) and 5 Saturdays (3, 10, 17, 24, 31).
        assertEquals(10, result.weekendDates.size)
        assertTrue(result.weekendDates.all { it.dayOfWeek == DayOfWeek.FRIDAY || it.dayOfWeek == DayOfWeek.SATURDAY })
        assertFalse(result.weekendDates.any { it.dayOfWeek == DayOfWeek.SUNDAY })
    }

    @Test
    fun testSingleCustomWeekendSundayOnly() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val weekend = setOf(DayOfWeek.SUNDAY)

        val result = calculator.calculate(start, end, weekend, inclusive = true)

        // October 2026 has 4 Sundays (4, 11, 18, 25)
        assertEquals(4, result.weekendDates.size)
        assertTrue(result.weekendDates.all { it.dayOfWeek == DayOfWeek.SUNDAY })
        // In Sunday-only mode, each Sunday present in range is a complete weekend of that week!
        assertEquals(4, result.completeWeekends)
        assertEquals(0, result.partialWeekends)
    }

    @Test
    fun testLargeRangePerformance() {
        val start = LocalDate.of(2000, 1, 1)
        val end = LocalDate.of(2030, 12, 31)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)

        val startTime = System.currentTimeMillis()
        val result = calculator.calculate(start, end, weekend, inclusive = true)
        val duration = System.currentTimeMillis() - startTime

        assertTrue(result.weekendDates.isNotEmpty())
        assertTrue("Calculation should be fast (< 500ms)", duration < 500)
    }
}
