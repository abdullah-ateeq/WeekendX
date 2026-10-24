package com.example.weekendx.data.model

data class WeekendXHistory(
    val id: String,
    val startDate: String,
    val endDate: String,
    val weekendDays: List<Int>,
    val inclusive: Boolean,
    val savedAt: Long
)
