package com.example.weekendx.ui.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.data.model.ThemeMode
import com.example.weekendx.data.model.WeekStartOption
import com.example.weekendx.data.model.WeekendSettingsData
import com.example.weekendx.data.model.WeekendViewMode
import com.example.weekendx.data.model.WeekendXHistory
import com.example.weekendx.data.model.WeekendXResult
import com.example.weekendx.data.repository.WeekendXPreferencesRepository
import com.example.weekendx.domain.calculator.WeekendCalculator
import com.example.weekendx.domain.calendar.CalendarVisualizer
import com.example.weekendx.domain.formatter.WeekendFormatter
import com.example.weekendx.domain.generator.SummaryGenerator
import com.example.weekendx.util.ShareUtil
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.temporal.TemporalAdjusters
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class WeekendXViewModel(
    application: Application
) : AndroidViewModel(application) {

    private val repository = WeekendXPreferencesRepository(application.applicationContext)
    private val calculator = WeekendCalculator()
    private val visualizer = CalendarVisualizer()
    private val generator = SummaryGenerator(calculator)
    val formatter = WeekendFormatter()

    private val _uiState = MutableStateFlow(WeekendXUiState())
    val uiState: StateFlow<WeekendXUiState> = _uiState.asStateFlow()

    init {
        loadInitialData()
    }

    private fun loadInitialData() {
        val settings = repository.loadSettings()
        val history = repository.loadHistory()

        val today = LocalDate.now()
        val (savedStart, savedEnd) = if (settings.restoreLastDates) {
            repository.loadLastDates()
        } else {
            Pair(null, null)
        }

        val initialStart = savedStart ?: today
        val initialEnd = savedEnd ?: today.with(TemporalAdjusters.lastDayOfMonth())

        _uiState.update { current ->
            current.copy(
                startDate = initialStart,
                endDate = initialEnd,
                weekendDays = settings.weekendDays,
                inclusive = settings.inclusive,
                settings = settings,
                history = history,
                previewMonth = YearMonth.from(initialStart)
            )
        }

        if (settings.liveCalculation) {
            calculateResults(initialStart, initialEnd, settings.weekendDays, settings.inclusive, settings)
        }
    }

    fun updateStartDate(newDate: LocalDate) {
        val current = _uiState.value
        _uiState.update {
            it.copy(
                startDate = newDate,
                previewMonth = YearMonth.from(newDate)
            )
        }
        persistDates(newDate, current.endDate)
        if (current.settings.liveCalculation) {
            calculateResults(newDate, current.endDate, current.weekendDays, current.inclusive, current.settings)
        }
    }

    fun updateEndDate(newDate: LocalDate) {
        val current = _uiState.value
        _uiState.update { it.copy(endDate = newDate) }
        persistDates(current.startDate, newDate)
        if (current.settings.liveCalculation) {
            calculateResults(current.startDate, newDate, current.weekendDays, current.inclusive, current.settings)
        }
    }

    fun setTodayForStart() {
        updateStartDate(LocalDate.now())
    }

    fun setTodayForEnd() {
        updateEndDate(LocalDate.now())
    }

    fun swapDates() {
        val current = _uiState.value
        val newStart = current.endDate
        val newEnd = current.startDate
        _uiState.update {
            it.copy(
                startDate = newStart,
                endDate = newEnd,
                previewMonth = YearMonth.from(newStart)
            )
        }
        persistDates(newStart, newEnd)
        if (current.settings.liveCalculation) {
            calculateResults(newStart, newEnd, current.weekendDays, current.inclusive, current.settings)
        }
    }

    fun applyQuickRange(rangeType: QuickRangeType) {
        val today = LocalDate.now()
        val (start, end) = when (rangeType) {
            QuickRangeType.TODAY -> today to today
            QuickRangeType.THIS_WEEK -> {
                val firstDay = visualizer.getFirstDayOfWeek(_uiState.value.settings.weekStart)
                val weekStart = today.with(TemporalAdjusters.previousOrSame(firstDay))
                val weekEnd = weekStart.plusDays(6L)
                weekStart to weekEnd
            }
            QuickRangeType.THIS_MONTH -> {
                val startMonth = today.with(TemporalAdjusters.firstDayOfMonth())
                val endMonth = today.with(TemporalAdjusters.lastDayOfMonth())
                startMonth to endMonth
            }
            QuickRangeType.NEXT_MONTH -> {
                val nextMonth = today.plusMonths(1L)
                val startMonth = nextMonth.with(TemporalAdjusters.firstDayOfMonth())
                val endMonth = nextMonth.with(TemporalAdjusters.lastDayOfMonth())
                startMonth to endMonth
            }
            QuickRangeType.NEXT_30_DAYS -> today to today.plusDays(30L)
            QuickRangeType.NEXT_90_DAYS -> today to today.plusDays(90L)
        }

        val current = _uiState.value
        _uiState.update {
            it.copy(
                startDate = start,
                endDate = end,
                previewMonth = YearMonth.from(start)
            )
        }
        persistDates(start, end)
        if (current.settings.liveCalculation) {
            calculateResults(start, end, current.weekendDays, current.inclusive, current.settings)
        }
    }

    fun setWeekendDays(newWeekendDays: Set<DayOfWeek>): Boolean {
        if (newWeekendDays.size >= 7) {
            _uiState.update { it.copy(errorMessage = "At least one non-weekend day must remain.") }
            return false
        }
        if (newWeekendDays.isEmpty()) {
            _uiState.update { it.copy(errorMessage = "Please select at least one weekend day.") }
            return false
        }

        val updatedSettings = _uiState.value.settings.copy(weekendDays = newWeekendDays)
        repository.saveSettings(updatedSettings)

        _uiState.update {
            it.copy(
                weekendDays = newWeekendDays,
                settings = updatedSettings,
                errorMessage = null
            )
        }

        if (updatedSettings.liveCalculation) {
            val current = _uiState.value
            calculateResults(current.startDate, current.endDate, newWeekendDays, current.inclusive, updatedSettings)
        }
        return true
    }

    fun setInclusive(inclusive: Boolean) {
        val updatedSettings = _uiState.value.settings.copy(inclusive = inclusive)
        repository.saveSettings(updatedSettings)

        val current = _uiState.value
        _uiState.update {
            it.copy(
                inclusive = inclusive,
                settings = updatedSettings
            )
        }

        if (updatedSettings.liveCalculation) {
            calculateResults(current.startDate, current.endDate, current.weekendDays, inclusive, updatedSettings)
        }
    }

    fun findWeekends() {
        val current = _uiState.value
        calculateResults(current.startDate, current.endDate, current.weekendDays, current.inclusive, current.settings)
    }

    private fun calculateResults(
        start: LocalDate,
        end: LocalDate,
        weekendDays: Set<DayOfWeek>,
        inclusive: Boolean,
        settings: WeekendSettingsData
    ) {
        val result = calculator.calculate(
            startDate = start,
            endDate = end,
            weekendDays = weekendDays,
            inclusive = inclusive
        )

        val firstDay = visualizer.getFirstDayOfWeek(settings.weekStart)
        val previewData = visualizer.generateMonthData(
            yearMonth = _uiState.value.previewMonth,
            firstDayOfWeek = firstDay,
            weekendDays = weekendDays,
            normalizedStart = result.normalizedStart,
            normalizedEnd = result.normalizedEnd,
            inclusive = inclusive
        )

        val monthly = if (settings.monthlySummary) generator.generateMonthlySummaries(result) else emptyList()
        val yearly = if (settings.yearlySummary) generator.generateYearlySummaries(result) else emptyList()

        _uiState.update {
            it.copy(
                result = result,
                calendarMonthData = previewData,
                monthlySummaries = monthly,
                yearlySummaries = yearly,
                errorMessage = null
            )
        }
    }

    fun reset() {
        val today = LocalDate.now()
        val defaultWeekend = setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        val defaultInclusive = true

        val currentSettings = _uiState.value.settings.copy(
            weekendDays = defaultWeekend,
            inclusive = defaultInclusive
        )
        repository.saveSettings(currentSettings)
        persistDates(today, today)

        _uiState.update {
            it.copy(
                startDate = today,
                endDate = today,
                weekendDays = defaultWeekend,
                inclusive = defaultInclusive,
                result = null,
                previewMonth = YearMonth.from(today),
                calendarMonthData = null,
                monthlySummaries = emptyList(),
                yearlySummaries = emptyList(),
                errorMessage = null,
                infoMessage = "Reset to defaults."
            )
        }

        if (currentSettings.liveCalculation) {
            calculateResults(today, today, defaultWeekend, defaultInclusive, currentSettings)
        }
    }

    fun saveCurrentToHistory() {
        val current = _uiState.value
        val updatedHistory = repository.addHistoryEntry(
            startDate = current.startDate,
            endDate = current.endDate,
            weekendDays = current.weekendDays,
            inclusive = current.inclusive
        )
        _uiState.update {
            it.copy(
                history = updatedHistory,
                infoMessage = "Saved to history."
            )
        }
    }

    fun restoreHistory(entry: WeekendXHistory) {
        val start = runCatching { LocalDate.parse(entry.startDate) }.getOrNull() ?: return
        val end = runCatching { LocalDate.parse(entry.endDate) }.getOrNull() ?: return
        val weekendDays = entry.weekendDays.mapNotNull {
            runCatching { DayOfWeek.of(it) }.getOrNull()
        }.toSet().ifEmpty {
            setOf(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY)
        }
        val inclusive = entry.inclusive

        val updatedSettings = _uiState.value.settings.copy(
            weekendDays = weekendDays,
            inclusive = inclusive
        )
        repository.saveSettings(updatedSettings)
        persistDates(start, end)

        _uiState.update {
            it.copy(
                startDate = start,
                endDate = end,
                weekendDays = weekendDays,
                inclusive = inclusive,
                settings = updatedSettings,
                previewMonth = YearMonth.from(start),
                infoMessage = "Search restored."
            )
        }

        // Always recalculate on history restore
        calculateResults(start, end, weekendDays, inclusive, updatedSettings)
    }

    fun deleteHistory(id: String) {
        val updatedHistory = repository.deleteHistoryEntry(id)
        _uiState.update {
            it.copy(
                history = updatedHistory,
                infoMessage = "Search removed."
            )
        }
    }

    fun clearHistory() {
        repository.clearHistory()
        _uiState.update {
            it.copy(
                history = emptyList(),
                infoMessage = "History cleared."
            )
        }
    }

    fun updateSettings(newSettings: WeekendSettingsData) {
        repository.saveSettings(newSettings)
        _uiState.update {
            it.copy(
                settings = newSettings,
                weekendDays = newSettings.weekendDays,
                inclusive = newSettings.inclusive
            )
        }
        val current = _uiState.value
        calculateResults(current.startDate, current.endDate, newSettings.weekendDays, newSettings.inclusive, newSettings)
    }

    fun resetSettings() {
        val defaultSettings = repository.resetSettings()
        _uiState.update {
            it.copy(
                settings = defaultSettings,
                weekendDays = defaultSettings.weekendDays,
                inclusive = defaultSettings.inclusive,
                infoMessage = "Settings restored to defaults."
            )
        }
        val current = _uiState.value
        calculateResults(current.startDate, current.endDate, defaultSettings.weekendDays, defaultSettings.inclusive, defaultSettings)
    }

    fun toggleViewMode() {
        val currentMode = _uiState.value.settings.weekendViewMode
        val nextMode = if (currentMode == WeekendViewMode.GROUPED) WeekendViewMode.COMPACT else WeekendViewMode.GROUPED
        val updatedSettings = _uiState.value.settings.copy(weekendViewMode = nextMode)
        repository.saveSettings(updatedSettings)
        _uiState.update { it.copy(settings = updatedSettings) }
    }

    fun navigatePreviewMonth(deltaMonths: Long) {
        val newYm = _uiState.value.previewMonth.plusMonths(deltaMonths)
        val current = _uiState.value
        val firstDay = visualizer.getFirstDayOfWeek(current.settings.weekStart)
        val normStart = current.result?.normalizedStart ?: current.startDate
        val normEnd = current.result?.normalizedEnd ?: current.endDate

        val previewData = visualizer.generateMonthData(
            yearMonth = newYm,
            firstDayOfWeek = firstDay,
            weekendDays = current.weekendDays,
            normalizedStart = normStart,
            normalizedEnd = normEnd,
            inclusive = current.inclusive
        )

        _uiState.update {
            it.copy(
                previewMonth = newYm,
                calendarMonthData = previewData
            )
        }
    }

    fun selectDateForDetails(date: LocalDate) {
        val current = _uiState.value
        val firstDay = visualizer.getFirstDayOfWeek(current.settings.weekStart)
        val details = formatter.getDateDetails(date, current.weekendDays, firstDay)
        _uiState.update { it.copy(selectedDateDetails = details) }
    }

    fun clearDateDetails() {
        _uiState.update { it.copy(selectedDateDetails = null) }
    }

    fun clearInfoMessage() {
        _uiState.update { it.copy(infoMessage = null) }
    }

    private fun persistDates(start: LocalDate, end: LocalDate) {
        if (_uiState.value.settings.restoreLastDates) {
            repository.saveLastDates(start, end)
        }
    }
}

enum class QuickRangeType {
    TODAY,
    THIS_WEEK,
    THIS_MONTH,
    NEXT_MONTH,
    NEXT_30_DAYS,
    NEXT_90_DAYS
}
