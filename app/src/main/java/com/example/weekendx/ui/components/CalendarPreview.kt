package com.example.weekendx.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.weekendx.data.model.CalendarMonthData
import java.time.LocalDate
import java.time.format.TextStyle
import java.time.temporal.WeekFields
import java.util.Locale

@Composable
fun CalendarPreview(
    monthData: CalendarMonthData?,
    showWeekNumbers: Boolean,
    onNavigateMonth: (delta: Long) -> Unit,
    onDateClick: (LocalDate) -> Unit,
    modifier: Modifier = Modifier
) {
    if (monthData == null) return

    val monthTitle = "${monthData.yearMonth.month.getDisplayName(TextStyle.FULL, Locale.getDefault())} ${monthData.yearMonth.year}"

    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        border = CardDefaults.outlinedCardBorder()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Month navigation header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { onNavigateMonth(-1L) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = "Previous month"
                    )
                }

                Text(
                    text = monthTitle.uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                IconButton(onClick = { onNavigateMonth(1L) }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Next month"
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Weekday headers row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                if (showWeekNumbers) {
                    Text(
                        text = "#",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }

                monthData.weekdayHeaders.forEach { dow ->
                    val headerText = dow.getDisplayName(TextStyle.SHORT, Locale.getDefault()).take(3).uppercase()
                    Text(
                        text = headerText,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Calendar weeks
            monthData.weeks.forEach { weekList ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 2.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (showWeekNumbers) {
                        val firstValidDay = weekList.firstOrNull { it != null }
                        val weekNum = firstValidDay?.date?.get(WeekFields.ISO.weekOfYear())?.toString() ?: ""
                        Text(
                            text = weekNum,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    weekList.forEach { calDay ->
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .aspectRatio(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            if (calDay != null) {
                                val isWeekendAndInRange = calDay.isWeekend && calDay.isInRange
                                val isWeekendOutOfRange = calDay.isWeekend && !calDay.isInRange

                                val cellBgModifier = when {
                                    isWeekendAndInRange -> Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .border(1.5.dp, MaterialTheme.colorScheme.primary, CircleShape)
                                    isWeekendOutOfRange -> Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .border(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.5f), CircleShape)
                                    calDay.isInRange -> Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f))
                                    else -> Modifier.size(32.dp)
                                }

                                val textColor = when {
                                    isWeekendAndInRange -> MaterialTheme.colorScheme.onPrimaryContainer
                                    calDay.isInRange -> MaterialTheme.colorScheme.onSurface
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                                }

                                val dayDesc = "${calDay.date.dayOfWeek.name}, ${calDay.date.dayOfMonth} ${monthData.yearMonth.month.name} ${calDay.date.year}" +
                                        (if (isWeekendAndInRange) ", weekend in range" else "")

                                Box(
                                    modifier = cellBgModifier
                                        .clickable { onDateClick(calDay.date) }
                                        .semantics { contentDescription = dayDesc },
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${calDay.date.dayOfMonth}",
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = if (isWeekendAndInRange || calDay.isToday) FontWeight.Bold else FontWeight.Normal,
                                        color = textColor,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
