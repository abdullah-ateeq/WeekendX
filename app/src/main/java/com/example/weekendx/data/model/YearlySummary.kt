package com.example.weekendx.data.model

data class YearlySummary(
    val year: Int,
    val weekendDays: Int,
    val completeWeekends: Int,
    val partialWeekends: Int,
    val calendarDays: Int
)
