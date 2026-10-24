package com.example.weekendx.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.data.model.ThemeMode
import com.example.weekendx.data.model.WeekStartOption
import com.example.weekendx.data.model.WeekendSettingsData
import com.example.weekendx.data.model.WeekendViewMode
import com.example.weekendx.data.model.WeekendXHistory
import java.time.DayOfWeek
import java.time.LocalDate
import org.json.JSONArray
import org.json.JSONObject

class WeekendXPreferencesRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "weekendx_preferences"

        private const val KEY_THEME_MODE = "key_theme_mode"
        private const val KEY_DYNAMIC_COLOR = "key_dynamic_color"
        private const val KEY_WEEKEND_DAYS = "key_weekend_days"
        private const val KEY_INCLUSIVE = "key_inclusive"
        private const val KEY_SIGNED_DIFFERENCE = "key_signed_difference"
        private const val KEY_LIVE_CALCULATION = "key_live_calculation"
        private const val KEY_CALENDAR_PREVIEW = "key_calendar_preview"
        private const val KEY_PERCENTAGE = "key_percentage"
        private const val KEY_PROGRESS = "key_progress"
        private const val KEY_MONTHLY_SUMMARY = "key_monthly_summary"
        private const val KEY_YEARLY_SUMMARY = "key_yearly_summary"
        private const val KEY_WEEK_NUMBERS = "key_week_numbers"
        private const val KEY_WEEK_START = "key_week_start"
        private const val KEY_DATE_FORMAT = "key_date_format"
        private const val KEY_HAPTIC = "key_haptic"
        private const val KEY_ANIMATIONS = "key_animations"
        private const val KEY_CONFIRM_DELETE = "key_confirm_delete"
        private const val KEY_RESTORE_LAST_DATES = "key_restore_last_dates"
        private const val KEY_VIEW_MODE = "key_view_mode"
        private const val KEY_LAST_START_DATE = "key_last_start_date"
        private const val KEY_LAST_END_DATE = "key_last_end_date"
        private const val KEY_HISTORY_JSON = "key_history_json"

        private const val MAX_HISTORY_COUNT = 10
    }

    fun loadSettings(): WeekendSettingsData {
        val themeModeStr = prefs.getString(KEY_THEME_MODE, ThemeMode.SYSTEM.name)
        val themeMode = runCatching { ThemeMode.valueOf(themeModeStr!!) }.getOrDefault(ThemeMode.SYSTEM)

        val dynamicColor = prefs.getBoolean(KEY_DYNAMIC_COLOR, true)

        val weekendDaysInts = prefs.getStringSet(
            KEY_WEEKEND_DAYS,
            setOf(DayOfWeek.SATURDAY.value.toString(), DayOfWeek.SUNDAY.value.toString())
        ) ?: setOf("6", "7")

        val weekendDays = weekendDaysInts.mapNotNull {
            it.toIntOrNull()?.let { value -> DayOfWeek.of(value) }
        }.toSet().ifEmpty {
            setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        }

        val inclusive = prefs.getBoolean(KEY_INCLUSIVE, true)
        val signedDiff = prefs.getBoolean(KEY_SIGNED_DIFFERENCE, false)
        val liveCalc = prefs.getBoolean(KEY_LIVE_CALCULATION, true)
        val calendarPreview = prefs.getBoolean(KEY_CALENDAR_PREVIEW, true)
        val percentage = prefs.getBoolean(KEY_PERCENTAGE, true)
        val progress = prefs.getBoolean(KEY_PROGRESS, true)
        val monthlySummary = prefs.getBoolean(KEY_MONTHLY_SUMMARY, false)
        val yearlySummary = prefs.getBoolean(KEY_YEARLY_SUMMARY, false)
        val weekNumbers = prefs.getBoolean(KEY_WEEK_NUMBERS, false)

        val weekStartStr = prefs.getString(KEY_WEEK_START, WeekStartOption.SYSTEM.name)
        val weekStart = runCatching { WeekStartOption.valueOf(weekStartStr!!) }.getOrDefault(WeekStartOption.SYSTEM)

        val dateFormatStr = prefs.getString(KEY_DATE_FORMAT, DateFormatOption.DAY_MONTH_YEAR.name)
        val dateFormat = runCatching { DateFormatOption.valueOf(dateFormatStr!!) }.getOrDefault(DateFormatOption.DAY_MONTH_YEAR)

        val haptic = prefs.getBoolean(KEY_HAPTIC, true)
        val animations = prefs.getBoolean(KEY_ANIMATIONS, true)
        val confirmDelete = prefs.getBoolean(KEY_CONFIRM_DELETE, true)
        val restoreLastDates = prefs.getBoolean(KEY_RESTORE_LAST_DATES, true)

        val viewModeStr = prefs.getString(KEY_VIEW_MODE, WeekendViewMode.GROUPED.name)
        val viewMode = runCatching { WeekendViewMode.valueOf(viewModeStr!!) }.getOrDefault(WeekendViewMode.GROUPED)

        return WeekendSettingsData(
            themeMode = themeMode,
            dynamicColor = dynamicColor,
            weekendDays = weekendDays,
            inclusive = inclusive,
            signedDifference = signedDiff,
            liveCalculation = liveCalc,
            calendarPreview = calendarPreview,
            weekendPercentage = percentage,
            progress = progress,
            monthlySummary = monthlySummary,
            yearlySummary = yearlySummary,
            weekNumbers = weekNumbers,
            weekStart = weekStart,
            dateFormat = dateFormat,
            hapticFeedback = haptic,
            animations = animations,
            confirmDelete = confirmDelete,
            restoreLastDates = restoreLastDates,
            weekendViewMode = viewMode
        )
    }

    fun saveSettings(settings: WeekendSettingsData) {
        prefs.edit()
            .putString(KEY_THEME_MODE, settings.themeMode.name)
            .putBoolean(KEY_DYNAMIC_COLOR, settings.dynamicColor)
            .putStringSet(KEY_WEEKEND_DAYS, settings.weekendDays.map { it.value.toString() }.toSet())
            .putBoolean(KEY_INCLUSIVE, settings.inclusive)
            .putBoolean(KEY_SIGNED_DIFFERENCE, settings.signedDifference)
            .putBoolean(KEY_LIVE_CALCULATION, settings.liveCalculation)
            .putBoolean(KEY_CALENDAR_PREVIEW, settings.calendarPreview)
            .putBoolean(KEY_PERCENTAGE, settings.weekendPercentage)
            .putBoolean(KEY_PROGRESS, settings.progress)
            .putBoolean(KEY_MONTHLY_SUMMARY, settings.monthlySummary)
            .putBoolean(KEY_YEARLY_SUMMARY, settings.yearlySummary)
            .putBoolean(KEY_WEEK_NUMBERS, settings.weekNumbers)
            .putString(KEY_WEEK_START, settings.weekStart.name)
            .putString(KEY_DATE_FORMAT, settings.dateFormat.name)
            .putBoolean(KEY_HAPTIC, settings.hapticFeedback)
            .putBoolean(KEY_ANIMATIONS, settings.animations)
            .putBoolean(KEY_CONFIRM_DELETE, settings.confirmDelete)
            .putBoolean(KEY_RESTORE_LAST_DATES, settings.restoreLastDates)
            .putString(KEY_VIEW_MODE, settings.weekendViewMode.name)
            .apply()
    }

    fun resetSettings(): WeekendSettingsData {
        val defaultSettings = WeekendSettingsData()
        saveSettings(defaultSettings)
        return defaultSettings
    }

    fun saveLastDates(startDate: LocalDate, endDate: LocalDate) {
        prefs.edit()
            .putString(KEY_LAST_START_DATE, startDate.toString())
            .putString(KEY_LAST_END_DATE, endDate.toString())
            .apply()
    }

    fun loadLastDates(): Pair<LocalDate?, LocalDate?> {
        val startStr = prefs.getString(KEY_LAST_START_DATE, null)
        val endStr = prefs.getString(KEY_LAST_END_DATE, null)
        val start = runCatching { startStr?.let { LocalDate.parse(it) } }.getOrNull()
        val end = runCatching { endStr?.let { LocalDate.parse(it) } }.getOrNull()
        return Pair(start, end)
    }

    fun loadHistory(): List<WeekendXHistory> {
        val jsonStr = prefs.getString(KEY_HISTORY_JSON, null) ?: return emptyList()
        return try {
            val jsonArray = JSONArray(jsonStr)
            val list = mutableListOf<WeekendXHistory>()
            for (i in 0 until jsonArray.length()) {
                val obj = jsonArray.getJSONObject(i)
                val id = obj.optString("id", "")
                val startDate = obj.optString("startDate", "")
                val endDate = obj.optString("endDate", "")
                val inclusive = obj.optBoolean("inclusive", true)
                val savedAt = obj.optLong("savedAt", 0L)
                val daysArray = obj.optJSONArray("weekendDays")
                val daysList = mutableListOf<Int>()
                if (daysArray != null) {
                    for (j in 0 until daysArray.length()) {
                        daysList.add(daysArray.getInt(j))
                    }
                }
                list.add(
                    WeekendXHistory(
                        id = id,
                        startDate = startDate,
                        endDate = endDate,
                        weekendDays = daysList,
                        inclusive = inclusive,
                        savedAt = savedAt
                    )
                )
            }
            list
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun persistHistory(history: List<WeekendXHistory>) {
        val jsonArray = JSONArray()
        history.forEach { item ->
            val obj = JSONObject().apply {
                put("id", item.id)
                put("startDate", item.startDate)
                put("endDate", item.endDate)
                put("inclusive", item.inclusive)
                put("savedAt", item.savedAt)
                val daysArr = JSONArray()
                item.weekendDays.forEach { daysArr.put(it) }
                put("weekendDays", daysArr)
            }
            jsonArray.put(obj)
        }
        prefs.edit().putString(KEY_HISTORY_JSON, jsonArray.toString()).apply()
    }

    fun addHistoryEntry(
        startDate: LocalDate,
        endDate: LocalDate,
        weekendDays: Set<DayOfWeek>,
        inclusive: Boolean
    ): List<WeekendXHistory> {
        val currentHistory = loadHistory().toMutableList()
        val startStr = startDate.toString()
        val endStr = endDate.toString()
        val sortedDays = weekendDays.map { it.value }.sorted()

        // Consecutive duplicate check
        if (currentHistory.isNotEmpty()) {
            val latest = currentHistory.first()
            if (latest.startDate == startStr &&
                latest.endDate == endStr &&
                latest.inclusive == inclusive &&
                latest.weekendDays.sorted() == sortedDays
            ) {
                return currentHistory
            }
        }

        val newEntry = WeekendXHistory(
            id = System.currentTimeMillis().toString(),
            startDate = startStr,
            endDate = endStr,
            weekendDays = sortedDays,
            inclusive = inclusive,
            savedAt = System.currentTimeMillis()
        )

        currentHistory.add(0, newEntry)

        // Enforce maximum 10 entries (FIFO: remove oldest)
        while (currentHistory.size > MAX_HISTORY_COUNT) {
            currentHistory.removeAt(currentHistory.lastIndex)
        }

        persistHistory(currentHistory)
        return currentHistory
    }

    fun deleteHistoryEntry(id: String): List<WeekendXHistory> {
        val currentHistory = loadHistory().toMutableList()
        currentHistory.removeAll { it.id == id }
        persistHistory(currentHistory)
        return currentHistory
    }

    fun clearHistory() {
        prefs.edit().remove(KEY_HISTORY_JSON).apply()
    }
}
