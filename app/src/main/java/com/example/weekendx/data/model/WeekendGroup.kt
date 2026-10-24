package com.example.weekendx.data.model

import java.time.DayOfWeek
import java.time.LocalDate

data class WeekendGroup(
    val groupNumber: Int,
    val dates: List<LocalDate>,
    val isComplete: Boolean,
    val label: String
)
