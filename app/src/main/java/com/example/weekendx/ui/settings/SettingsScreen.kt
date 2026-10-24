package com.example.weekendx.ui.settings

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.data.model.ThemeMode
import com.example.weekendx.data.model.WeekStartOption
import com.example.weekendx.ui.components.ConfirmDialog
import com.example.weekendx.ui.home.WeekendXViewModel
import com.example.weekendx.util.HapticUtil
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: WeekendXViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val settings = uiState.settings
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    var showResetConfirm by remember { mutableStateOf(false) }
    var showClearHistoryConfirm by remember { mutableStateOf(false) }

    LaunchedEffect(uiState.errorMessage) {
        uiState.errorMessage?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Settings",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // APPEARANCE
            item {
                SettingsSection(title = "APPEARANCE") {
                    Text(
                        text = "Theme",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    ThemeMode.values().forEach { mode ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(themeMode = mode))
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.themeMode == mode,
                                onClick = {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(themeMode = mode))
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = mode.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Dynamic Color",
                        subtitle = "Android 12+ wallpaper tinting",
                        checked = settings.dynamicColor,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(dynamicColor = it))
                        }
                    )
                }
            }

            // WEEKEND DAYS
            item {
                SettingsSection(title = "WEEKEND DAYS") {
                    Text(
                        text = "Select one or more weekend days (at least one non-weekend day must remain):",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                    )

                    DayOfWeek.values().forEach { dow ->
                        val dayName = dow.getDisplayName(TextStyle.FULL, Locale.getDefault())
                        val isChecked = settings.weekendDays.contains(dow)

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    val newSet = if (isChecked) {
                                        settings.weekendDays - dow
                                    } else {
                                        settings.weekendDays + dow
                                    }
                                    if (newSet.size >= 7) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("At least one non-weekend day must remain.")
                                        }
                                    } else if (newSet.isEmpty()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please select at least one weekend day.")
                                        }
                                    } else {
                                        viewModel.setWeekendDays(newSet)
                                    }
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Checkbox(
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    val newSet = if (checked) {
                                        settings.weekendDays + dow
                                    } else {
                                        settings.weekendDays - dow
                                    }
                                    if (newSet.size >= 7) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("At least one non-weekend day must remain.")
                                        }
                                    } else if (newSet.isEmpty()) {
                                        scope.launch {
                                            snackbarHostState.showSnackbar("Please select at least one weekend day.")
                                        }
                                    } else {
                                        viewModel.setWeekendDays(newSet)
                                    }
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = dayName,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isChecked) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // CALCULATION SETTINGS
            item {
                SettingsSection(title = "CALCULATION SETTINGS") {
                    SettingsSwitchRow(
                        title = "Include Start & End Dates",
                        subtitle = "Inclusive mode includes both the start and end dates.",
                        checked = settings.inclusive,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.setInclusive(it)
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Live Calculation",
                        subtitle = "Update automatically whenever dates or rules change",
                        checked = settings.liveCalculation,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(liveCalculation = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Signed Difference",
                        subtitle = "Show negative indicator on reversed ranges",
                        checked = settings.signedDifference,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(signedDifference = it))
                        }
                    )
                }
            }

            // DISPLAY SETTINGS
            item {
                SettingsSection(title = "DISPLAY SETTINGS") {
                    SettingsSwitchRow(
                        title = "Show Calendar Preview",
                        subtitle = "Display compact monthly calendar grid",
                        checked = settings.calendarPreview,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(calendarPreview = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Weekend Percentage",
                        subtitle = "Display weekend share of calendar days",
                        checked = settings.weekendPercentage,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(weekendPercentage = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Progress",
                        subtitle = "Display subtle progress bar indicator",
                        checked = settings.progress,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(progress = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Monthly Summary",
                        subtitle = "Break down multi-month ranges by month",
                        checked = settings.monthlySummary,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(monthlySummary = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Yearly Summary",
                        subtitle = "Break down multi-year ranges by year",
                        checked = settings.yearlySummary,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(yearlySummary = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Show Week Numbers",
                        subtitle = "Show ISO week number in calendar view",
                        checked = settings.weekNumbers,
                        onCheckedChange = {
                            HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                            viewModel.updateSettings(settings.copy(weekNumbers = it))
                        }
                    )
                }
            }

            // WEEK START
            item {
                SettingsSection(title = "WEEK STARTS ON") {
                    WeekStartOption.values().forEach { option ->
                        val label = when (option) {
                            WeekStartOption.SYSTEM -> "System default"
                            WeekStartOption.MONDAY -> "Monday"
                            WeekStartOption.SUNDAY -> "Sunday"
                        }
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(weekStart = option))
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.weekStart == option,
                                onClick = {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(weekStart = option))
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = label, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // DATE FORMAT
            item {
                SettingsSection(title = "DATE FORMAT") {
                    DateFormatOption.values().forEach { option ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(dateFormat = option))
                                }
                                .padding(horizontal = 16.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = settings.dateFormat == option,
                                onClick = {
                                    HapticUtil.performLightHaptic(view, settings.hapticFeedback)
                                    viewModel.updateSettings(settings.copy(dateFormat = option))
                                }
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(text = option.patternName, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }

            // BEHAVIOR
            item {
                SettingsSection(title = "BEHAVIOR") {
                    SettingsSwitchRow(
                        title = "Haptic Feedback",
                        subtitle = "Subtle tactile response on taps and actions",
                        checked = settings.hapticFeedback,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(hapticFeedback = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Animations",
                        subtitle = "Smooth 200–300 ms transitions",
                        checked = settings.animations,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(animations = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Confirm Delete",
                        subtitle = "Prompt before deleting saved searches",
                        checked = settings.confirmDelete,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(confirmDelete = it))
                        }
                    )

                    HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp), color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))

                    SettingsSwitchRow(
                        title = "Restore Last Dates",
                        subtitle = "Remember last selected start and end dates on app launch",
                        checked = settings.restoreLastDates,
                        onCheckedChange = {
                            viewModel.updateSettings(settings.copy(restoreLastDates = it))
                        }
                    )
                }
            }

            // RESET ACTIONS
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = { showResetConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Reset Settings to Default")
                    }

                    OutlinedButton(
                        onClick = { showClearHistoryConfirm = true },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "Clear All Search History",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Reset settings confirmation
    if (showResetConfirm) {
        ConfirmDialog(
            title = "Reset Settings?",
            message = "Restore all settings to their original defaults? History will not be deleted.",
            confirmText = "Reset",
            onConfirm = {
                viewModel.resetSettings()
                showResetConfirm = false
            },
            onDismiss = { showResetConfirm = false }
        )
    }

    // Clear history confirmation
    if (showClearHistoryConfirm) {
        ConfirmDialog(
            title = "Clear All Searches?",
            message = "Clear all saved searches? This action cannot be undone.",
            confirmText = "Clear",
            onConfirm = {
                viewModel.clearHistory()
                showClearHistoryConfirm = false
            },
            onDismiss = { showClearHistoryConfirm = false }
        )
    }
}

@Composable
private fun SettingsSection(
    title: String,
    content: @Composable () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            letterSpacing = 1.2.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.secondary,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            border = CardDefaults.outlinedCardBorder()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SettingsSwitchRow(
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .clickable { onCheckedChange(!checked) }
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        Spacer(modifier = Modifier.width(16.dp))
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}
