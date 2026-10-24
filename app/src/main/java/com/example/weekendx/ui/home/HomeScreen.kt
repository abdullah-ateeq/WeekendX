package com.example.weekendx.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ViewList
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ViewModule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.WeekendViewMode
import com.example.weekendx.ui.components.CalendarPreview
import com.example.weekendx.ui.components.CompactWeekendList
import com.example.weekendx.ui.components.DateDetailDialog
import com.example.weekendx.ui.components.DateRangeSelector
import com.example.weekendx.ui.components.MonthlySummarySection
import com.example.weekendx.ui.components.QuickChips
import com.example.weekendx.ui.components.ResultCard
import com.example.weekendx.ui.components.SaturdaySundayBreakdown
import com.example.weekendx.ui.components.WeekendGroupList
import com.example.weekendx.ui.components.WeekendSummaryCard
import com.example.weekendx.ui.components.YearlySummarySection
import com.example.weekendx.util.HapticUtil
import com.example.weekendx.util.ShareUtil

@Composable
fun HomeScreen(
    viewModel: WeekendXViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    val view = LocalView.current
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.infoMessage) {
        uiState.infoMessage?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearInfoMessage()
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            item {
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = "WeekendX",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Find weekends within a date range.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            // Quick Range Chips
            item {
                QuickChips(
                    hapticsEnabled = uiState.settings.hapticFeedback,
                    onSelectRange = { viewModel.applyQuickRange(it) }
                )
            }

            // Date Range Selector (Start Date, End Date, Today, Swap)
            item {
                DateRangeSelector(
                    startDate = uiState.startDate,
                    endDate = uiState.endDate,
                    dateFormat = uiState.settings.dateFormat,
                    formatter = viewModel.formatter,
                    hapticsEnabled = uiState.settings.hapticFeedback,
                    onStartDateChange = { viewModel.updateStartDate(it) },
                    onEndDateChange = { viewModel.updateEndDate(it) },
                    onSetTodayStart = { viewModel.setTodayForStart() },
                    onSetTodayEnd = { viewModel.setTodayForEnd() },
                    onSwap = { viewModel.swapDates() }
                )
            }

            // Find Weekends Button
            item {
                Button(
                    onClick = {
                        HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                        viewModel.findWeekends()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp),
                    shape = RoundedCornerShape(14.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "FIND WEEKENDS",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                }
            }

            // Primary Result Card
            uiState.result?.let { result ->
                item {
                    ResultCard(
                        result = result,
                        showSignedDifference = uiState.settings.signedDifference,
                        dateFormat = uiState.settings.dateFormat,
                        formatter = viewModel.formatter
                    )
                }

                // Actions Row: [ COPY RESULTS ] [ COPY DATES ] [ SAVE ] [ SHARE ] [ RESET ]
                item {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            FilledTonalButton(
                                onClick = {
                                    HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                                    val text = viewModel.formatter.formatFullResults(result, uiState.settings.dateFormat)
                                    ShareUtil.copyToClipboard(context, "WeekendX Results", text)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("COPY", style = MaterialTheme.typography.labelMedium)
                            }

                            FilledTonalButton(
                                onClick = {
                                    HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                                    viewModel.saveCurrentToHistory()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.BookmarkBorder, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SAVE", style = MaterialTheme.typography.labelMedium)
                            }

                            FilledTonalButton(
                                onClick = {
                                    HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                                    val text = viewModel.formatter.formatFullResults(result, uiState.settings.dateFormat)
                                    ShareUtil.shareText(context, "WeekendX Results", text)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("SHARE", style = MaterialTheme.typography.labelMedium)
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedButton(
                                onClick = {
                                    HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                                    val text = viewModel.formatter.formatDatesOnly(result, uiState.settings.dateFormat)
                                    ShareUtil.copyToClipboard(context, "Weekend Dates", text)
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("COPY DATES", style = MaterialTheme.typography.labelSmall)
                            }

                            OutlinedButton(
                                onClick = {
                                    HapticUtil.performLightHaptic(view, uiState.settings.hapticFeedback)
                                    viewModel.reset()
                                },
                                modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("RESET", style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }

                // Summary Statistics Card
                item {
                    WeekendSummaryCard(
                        result = result,
                        showPercentage = uiState.settings.weekendPercentage,
                        showProgress = uiState.settings.progress
                    )
                }

                // Breakdown by Saturday / Sunday (or configured weekend days)
                item {
                    SaturdaySundayBreakdown(
                        breakdown = result.dayOfWeekBreakdown,
                        onDateClick = { viewModel.selectDateForDetails(it) }
                    )
                }

                // Calendar Preview
                if (uiState.settings.calendarPreview) {
                    item {
                        CalendarPreview(
                            monthData = uiState.calendarMonthData,
                            showWeekNumbers = uiState.settings.weekNumbers,
                            onNavigateMonth = { viewModel.navigatePreviewMonth(it) },
                            onDateClick = { viewModel.selectDateForDetails(it) }
                        )
                    }
                }

                // Weekend Date List Header & View Mode Switcher
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "WEEKEND DATES",
                            style = MaterialTheme.typography.labelLarge,
                            letterSpacing = 1.2.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.secondary
                        )

                        IconButton(onClick = { viewModel.toggleViewMode() }) {
                            Icon(
                                imageVector = if (uiState.settings.weekendViewMode == WeekendViewMode.GROUPED) {
                                    Icons.Default.ViewModule
                                } else {
                                    Icons.AutoMirrored.Filled.ViewList
                                },
                                contentDescription = "Toggle view mode",
                                tint = MaterialTheme.colorScheme.secondary
                            )
                        }
                    }
                }

                // List Items (Grouped or Compact)
                if (result.weekendDates.isEmpty()) {
                    item {
                        Card(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(14.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surface
                            ),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No weekend dates found.",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else if (uiState.settings.weekendViewMode == WeekendViewMode.GROUPED) {
                    item {
                        WeekendGroupList(
                            groups = result.weekendGroups,
                            dateFormat = uiState.settings.dateFormat,
                            formatter = viewModel.formatter,
                            onDateClick = { viewModel.selectDateForDetails(it) }
                        )
                    }
                } else {
                    item {
                        CompactWeekendList(
                            breakdown = result.dayOfWeekBreakdown,
                            onDateClick = { viewModel.selectDateForDetails(it) }
                        )
                    }
                }

                // Monthly Summary
                if (uiState.settings.monthlySummary) {
                    item {
                        MonthlySummarySection(summaries = uiState.monthlySummaries)
                    }
                }

                // Yearly Summary
                if (uiState.settings.yearlySummary) {
                    item {
                        YearlySummarySection(summaries = uiState.yearlySummaries)
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }

    // Date Details Dialog
    DateDetailDialog(
        details = uiState.selectedDateDetails,
        dateFormat = uiState.settings.dateFormat,
        formatter = viewModel.formatter,
        onDismiss = { viewModel.clearDateDetails() }
    )
}
