package com.example.weekendx

import com.example.weekendx.domain.calculator.WeekendCalculator
import com.example.weekendx.domain.generator.SummaryGenerator
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class SummaryGeneratorTest {

    private lateinit var calculator: WeekendCalculator
    private lateinit var generator: SummaryGenerator

    @Before
    fun setUp() {
        calculator = WeekendCalculator()
        generator = SummaryGenerator(calculator)
    }

    @Test
    fun testOctober2026MonthlySummary() {
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)
        val result = calculator.calculate(start, end, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), inclusive = true)

        val summaries = generator.generateMonthlySummaries(result)
        assertEquals(1, summaries.size)

        val octSummary = summaries[0]
        assertEquals(YearMonth.of(2026, 10), octSummary.yearMonth)
        assertEquals(9, octSummary.weekendDays)
        assertEquals(4, octSummary.completeWeekends)
        assertEquals(1, octSummary.partialWeekends)
        assertEquals(31, octSummary.calendarDays)
    }

    @Test
    fun testMultiMonthSummary() {
        // Oct 2026 to Dec 2026
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 12, 31)
        val result = calculator.calculate(start, end, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), inclusive = true)

        val summaries = generator.generateMonthlySummaries(result)
        assertEquals(3, summaries.size)

        // October 2026: 9 weekend days
        assertEquals(9, summaries[0].weekendDays)
        // November 2026: 9 weekend days
        assertEquals(9, summaries[1].weekendDays)
        // December 2026: 8 weekend days
        assertEquals(8, summaries[2].weekendDays)
    }

    @Test
    fun testYearlySummary() {
        val start = LocalDate.of(2026, 1, 1)
        val end = LocalDate.of(2027, 12, 31)
        val result = calculator.calculate(start, end, setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY), inclusive = true)

        val yearly = generator.generateYearlySummaries(result)
        assertEquals(2, yearly.size)

        // 2026 and 2027 standard weekend days (52 weeks * 2 = 104)
        assertEquals(2026, yearly[0].year)
        assertEquals(104, yearly[0].weekendDays)
        assertEquals(2027, yearly[1].year)
        assertEquals(104, yearly[1].weekendDays)
    }
}
