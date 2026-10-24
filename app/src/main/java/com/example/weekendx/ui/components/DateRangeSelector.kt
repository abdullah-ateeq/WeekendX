package com.example.weekendx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.SwapVert
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.DateFormatOption
import com.example.weekendx.domain.formatter.WeekendFormatter
import com.example.weekendx.util.HapticUtil
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangeSelector(
    startDate: LocalDate,
    endDate: LocalDate,
    dateFormat: DateFormatOption,
    formatter: WeekendFormatter,
    hapticsEnabled: Boolean,
    onStartDateChange: (LocalDate) -> Unit,
    onEndDateChange: (LocalDate) -> Unit,
    onSetTodayStart: () -> Unit,
    onSetTodayEnd: () -> Unit,
    onSwap: () -> Unit,
    modifier: Modifier = Modifier
) {
    val view = LocalView.current
    var isPickingStart by remember { mutableStateOf(false) }
    var isPickingEnd by remember { mutableStateOf(false) }

    val startFormatted = formatter.formatDate(startDate, dateFormat)
    val startDayOfWeek = startDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    val endFormatted = formatter.formatDate(endDate, dateFormat)
    val endDayOfWeek = endDate.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.getDefault())

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Start Date Card
        DateBox(
            label = "START DATE",
            dateFormatted = startFormatted,
            dayOfWeek = startDayOfWeek,
            contentDesc = "Start date, $startFormatted.",
            onClickChange = {
                HapticUtil.performLightHaptic(view, hapticsEnabled)
                isPickingStart = true
            },
            onClickToday = {
                HapticUtil.performLightHaptic(view, hapticsEnabled)
                onSetTodayStart()
            }
        )

        // Swap Row
        Box(
            modifier = Modifier.fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            FilledTonalButton(
                onClick = {
                    HapticUtil.performLightHaptic(view, hapticsEnabled)
                    onSwap()
                },
                shape = CircleShape,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Icon(
                    imageVector = Icons.Default.SwapVert,
                    contentDescription = "Swap start and end dates",
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "SWAP",
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // End Date Card
        DateBox(
            label = "END DATE",
            dateFormatted = endFormatted,
            dayOfWeek = endDayOfWeek,
            contentDesc = "End date, $endFormatted.",
            onClickChange = {
                HapticUtil.performLightHaptic(view, hapticsEnabled)
                isPickingEnd = true
            },
            onClickToday = {
                HapticUtil.performLightHaptic(view, hapticsEnabled)
                onSetTodayEnd()
            }
        )
    }

    // Date Picker Dialog for Start Date
    if (isPickingStart) {
        val startEpochMillis = remember(startDate) {
            startDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = startEpochMillis)

        DatePickerDialog(
            onDismissRequest = { isPickingStart = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                            onStartDateChange(selected)
                            HapticUtil.performLightHaptic(view, hapticsEnabled)
                        }
                        isPickingStart = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isPickingStart = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // Date Picker Dialog for End Date
    if (isPickingEnd) {
        val endEpochMillis = remember(endDate) {
            endDate.atStartOfDay(ZoneId.of("UTC")).toInstant().toEpochMilli()
        }
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = endEpochMillis)

        DatePickerDialog(
            onDismissRequest = { isPickingEnd = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { millis ->
                            val selected = Instant.ofEpochMilli(millis).atZone(ZoneId.of("UTC")).toLocalDate()
                            onEndDateChange(selected)
                            HapticUtil.performLightHaptic(view, hapticsEnabled)
                        }
                        isPickingEnd = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { isPickingEnd = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }
}

@Composable
private fun DateBox(
    label: String,
    dateFormatted: String,
    dayOfWeek: String,
    contentDesc: String,
    onClickChange: () -> Unit,
    onClickToday: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClickChange() }
            .semantics { contentDescription = contentDesc },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    letterSpacing = 1.2.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.secondary
                )

                TextButton(
                    onClick = onClickToday,
                    modifier = Modifier.height(28.dp)
                ) {
                    Text(
                        text = "TODAY",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = dateFormatted,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = dayOfWeek,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = "Select date",
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}
