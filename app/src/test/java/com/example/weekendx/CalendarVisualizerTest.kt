package com.example.weekendx

import com.example.weekendx.data.model.WeekStartOption
import com.example.weekendx.domain.calendar.CalendarVisualizer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class CalendarVisualizerTest {

    private lateinit var visualizer: CalendarVisualizer

    @Before
    fun setUp() {
        visualizer = CalendarVisualizer()
    }

    @Test
    fun testWeekdayHeaders() {
        val mondayHeaders = visualizer.getWeekdayHeaders(DayOfWeek.MONDAY)
        assertEquals(7, mondayHeaders.size)
        assertEquals(DayOfWeek.MONDAY, mondayHeaders.first())
        assertEquals(DayOfWeek.SUNDAY, mondayHeaders.last())

        val sundayHeaders = visualizer.getWeekdayHeaders(DayOfWeek.SUNDAY)
        assertEquals(7, sundayHeaders.size)
        assertEquals(DayOfWeek.SUNDAY, sundayHeaders.first())
        assertEquals(DayOfWeek.SATURDAY, sundayHeaders.last())
    }

    @Test
    fun testOctober2026CalendarData() {
        val ym = YearMonth.of(2026, 10)
        val weekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val start = LocalDate.of(2026, 10, 1)
        val end = LocalDate.of(2026, 10, 31)

        val monthData = visualizer.generateMonthData(
            yearMonth = ym,
            firstDayOfWeek = DayOfWeek.MONDAY,
            weekendDays = weekend,
            normalizedStart = start,
            normalizedEnd = end,
            inclusive = true
        )

        assertEquals(ym, monthData.yearMonth)
        assertTrue(monthData.weeks.isNotEmpty())

        // Oct 1 2026 is Thursday. In Monday-start week: Mon, Tue, Wed are padding (null).
        val firstWeek = monthData.weeks.first()
        assertEquals(null, firstWeek[0])
        assertEquals(null, firstWeek[1])
        assertEquals(null, firstWeek[2])
        assertNotNull(firstWeek[3])
        assertEquals(1, firstWeek[3]?.date?.dayOfMonth)
        assertEquals(DayOfWeek.THURSDAY, firstWeek[3]?.date?.dayOfWeek)

        // Saturday Oct 3 and Sunday Oct 4
        val sat3 = firstWeek[5]
        val sun4 = firstWeek[6]
        assertNotNull(sat3)
        assertNotNull(sun4)
        assertTrue(sat3!!.isWeekend)
        assertTrue(sun4!!.isWeekend)
        assertTrue(sat3.isInRange)
        assertTrue(sun4.isInRange)
    }
}
